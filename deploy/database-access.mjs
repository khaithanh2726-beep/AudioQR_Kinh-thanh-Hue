import assert from 'node:assert/strict'
import {execFileSync} from 'node:child_process'
import {readFile,writeFile,mkdir,lstat} from 'node:fs/promises'
import {dirname,join} from 'node:path'
import {fileURLToPath} from 'node:url'
import {createHash} from 'node:crypto'

const [action='start']=process.argv.slice(2)
assert(['start','stop','status'].includes(action),'database-access.mjs start|stop|status')
const config=JSON.parse((await readFile(join(process.env.USERPROFILE,'AudioQR-CD/config.json'),'utf8')).replace(/^\uFEFF/,''))
const runtime=JSON.parse((await readFile(join(config.runtimeDirectory,'runtime.json'),'utf8')).replace(/^\uFEFF/,''))
assert.match(runtime.project,/^(audioqr-hue|hueqr-rehearsal-[a-f0-9]{8})$/)
const docker=args=>execFileSync('docker',args,{encoding:'utf8',windowsHide:true,stdio:['ignore','pipe','pipe'],timeout:120000,maxBuffer:2000000}).trim()
const name=runtime.project+'-local-db-access'
const existing=docker(['ps','-aq','--filter','name=^/'+name+'$'])
if(existing) {
  const c=JSON.parse(docker(['inspect',existing]))[0]
  assert.equal(c.Config.Labels['vn.hueqr.purpose'],'local-db-access')
  assert.equal(c.HostConfig.PortBindings['3306/tcp'][0].HostIp,'127.0.0.1')
  assert.equal(c.HostConfig.PortBindings['3306/tcp'][0].HostPort,'3307')
  assert(c.Mounts.every(m=>m.Type==='bind'&&!m.RW))
  if(action==='stop')docker(['stop',name])
  if(action==='start') {
    const db=JSON.parse(docker(['inspect',runtime.database]))[0]
    assert(db.State.Running,'Start the main database first')
    assert.equal(db.Config.Labels['com.docker.compose.project'],runtime.project)
    assert(c.NetworkSettings.Networks[runtime.project+'_default'])
    docker(['start',name])
  }
  console.log(JSON.stringify({status:action==='stop'?'STOPPED':action==='status'&&!c.State.Running?'STOPPED':'AVAILABLE',host:'127.0.0.1',port:3307,database:'hueqr',username:'hueqr'}))
} else if(action==='start') {
  const db=JSON.parse(docker(['inspect',runtime.database]))[0],app=JSON.parse(docker(['inspect',runtime.application]))[0]
  assert(db.State.Running,'Start the main database first')
  assert.equal(db.Config.Labels['com.docker.compose.project'],runtime.project)
  const network=runtime.project+'_default'
  assert(db.NetworkSettings.Networks[network])
  const root=join(process.env.LOCALAPPDATA,'AudioQR-Database-Access')
  await mkdir(root,{recursive:true})
  assert(!(await lstat(root)).isSymbolicLink())
  const sid=execFileSync('powershell.exe',['-NoProfile','-Command','[Security.Principal.WindowsIdentity]::GetCurrent().User.Value'],{encoding:'utf8',windowsHide:true,stdio:'pipe'}).trim()
  execFileSync('icacls.exe',[root,'/inheritance:r','/grant:r',`*${sid}:(OI)(CI)F`,'*S-1-5-18:(OI)(CI)F'],{stdio:'pipe',windowsHide:true})
  const source=join(dirname(fileURLToPath(import.meta.url)),'LocalMysqlRelay.java')
  execFileSync('javac',['--release','21','-d',root,source],{stdio:'pipe',windowsHide:true})
  const checksum=createHash('sha256').update(await readFile(source)).digest('hex')
  docker(['create','--name',name,'--network',network,'--publish','127.0.0.1:3307:3306','--label','vn.hueqr.purpose=local-db-access',
    '--label','com.docker.compose.project='+runtime.project,'--label','com.docker.compose.service=local-db-access','--label','com.docker.compose.oneoff=True',
    '--read-only','--cap-drop','ALL','--security-opt','no-new-privileges:true','--user','1000:1000','--pids-limit','64','--memory','128m',
    '--log-driver','local','--log-opt','max-size=1m','--log-opt','max-file=2','--mount','type=bind,source='+root+',target=/relay,readonly',
    '--entrypoint','java',app.Image,'-XX:ActiveProcessorCount=2','-cp','/relay','LocalMysqlRelay',runtime.database])
  docker(['start',name])
  await writeFile(join(root,'installation.json'),JSON.stringify({container:name,project:runtime.project,sourceSha256:checksum,port:'127.0.0.1:3307',passwordStored:false,sourceImage:app.Image,createdAt:new Date().toISOString()},null,2))
  console.log(JSON.stringify({status:'CREATED',host:'127.0.0.1',port:3307,database:'hueqr',username:'hueqr',databaseContainerUnchanged:true}))
} else console.log(JSON.stringify({status:'NOT_CREATED'}))
