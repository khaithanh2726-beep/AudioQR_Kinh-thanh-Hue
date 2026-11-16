import assert from 'node:assert/strict'
import {execFileSync} from 'node:child_process'
import {readFile,writeFile,lstat} from 'node:fs/promises'
import {join,resolve,dirname} from 'node:path'
import {fileURLToPath} from 'node:url'
import {randomUUID} from 'node:crypto'

// Chỉ khởi động container đã tồn tại; không tạo volume/schema/khóa hay build/pull ảnh.
export function mayResume(project,guard,receipt) {
  return guard.mode==='FROZEN' && receipt?.project===project && receipt.status==='STOPPED'
    && guard.owner===receipt.owner && /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/.test(receipt.owner||'')
}

export function validateContainers(runtime,app,db) {
  assert.match(runtime.project,/^(audioqr-hue|hueqr-rehearsal-[a-f0-9]{8})$/)
  for(const [value,service] of [[app,'application'],[db,'database']]) {
    assert.equal(value.Name,'/'+runtime.project+'-'+service+'-1')
    assert.equal(value.Config.Labels['com.docker.compose.project'],runtime.project)
    assert.equal(value.Config.Labels['com.docker.compose.service'],service)
    const destination=service==='application'?'/data/audio':'/var/lib/mysql'
    assert(value.Mounts.some(m=>m.Type==='volume'&&m.Name===(runtime.volumeNames?.[service]||runtime.project+'_'+(service==='application'?'audio-data':'mysql-data'))&&m.Destination===destination))
  }
  assert.equal(db.HostConfig.PortBindings?.['3306/tcp'],undefined)
  assert(app.HostConfig.PortBindings?.['8080/tcp']?.every(p=>p.HostIp==='127.0.0.1'))
}

async function main() {
  const [action,runtimeArg]=process.argv.slice(2)
  assert(['start','stop','status'].includes(action),'Usage: quick-run.mjs start|stop|status [runtime-directory]')
  let runtimeDirectory=runtimeArg
  if(!runtimeDirectory) {
    const config=JSON.parse((await readFile(join(process.env.USERPROFILE,'AudioQR-CD/config.json'),'utf8')).replace(/^\uFEFF/,''))
    runtimeDirectory=config.runtimeDirectory
  }
  const directory=resolve(runtimeDirectory)
  assert((await lstat(directory)).isDirectory()&&!(await lstat(directory)).isSymbolicLink())
  const runtime=JSON.parse((await readFile(join(directory,'runtime.json'),'utf8')).replace(/^\uFEFF/,''))
  const origin=new URL(runtime.url)
  assert(origin.protocol==='http:'&&['localhost','127.0.0.1'].includes(origin.hostname))
  const docker=(args,input)=>execFileSync('docker',args,{input,encoding:'utf8',windowsHide:true,timeout:120000,stdio:['pipe','pipe','pipe'],maxBuffer:2000000}).trim()
  const inspect=name=>JSON.parse(docker(['inspect',name]))[0]
  const maintenance=(...args)=>JSON.parse(execFileSync(process.execPath,[join(dirname(fileURLToPath(import.meta.url)),'maintenance.mjs'),directory,...args],{encoding:'utf8',windowsHide:true,timeout:300000,stdio:['ignore','pipe','pipe']}).trim())
  const sql=q=>docker(['exec','-i',runtime.database,'sh','-c','MYSQL_PWD="$MYSQL_PASSWORD" exec mysql -u hueqr -N -B hueqr'],q)
  const recordPath=join(directory,'quick-run-state.json')
  let receipt=null
  try {receipt=JSON.parse(await readFile(recordPath,'utf8'))}catch(e){if(e.code!=='ENOENT')throw e}
  const save=()=>writeFile(recordPath,JSON.stringify(receipt,null,2))
  const app=inspect(runtime.application),db=inspect(runtime.database)
  validateContainers(runtime,app,db)
  const report={startedAt:new Date().toISOString(),action,project:runtime.project,status:'RUNNING',publicTunnelStarted:false}
  async function waitFor(check,label,attempts=90) {
    for(let i=0;i<attempts;i++){try{if(await check())return}catch{};await new Promise(r=>setTimeout(r,1000))}
    throw new Error(label)
  }
  if(action==='status') {
    report.applicationRunning=app.State.Running;report.databaseRunning=db.State.Running
    if(db.State.Running)report.gate=maintenance('status').mode
  } else if(action==='start') {
    console.log('Dang khoi dong database da luu...')
    if(!db.State.Running)docker(['start',runtime.database])
    await waitFor(()=>sql('SELECT 1;')==='1','DATABASE_NOT_READY')
    const guard=maintenance('status')
    if(guard.mode==='RUNNING') {
      if(!app.State.Running)docker(['start',runtime.application])
    } else {
      assert(mayResume(runtime.project,guard,receipt),'MAINTENANCE_OWNED_ELSEWHERE: giu gate, can doi chieu owner; khong tu mo')
      maintenance('resume',receipt.owner)
    }
    await waitFor(async()=>{
      const response=await fetch(new URL('/api/v1/auth/csrf',origin),{signal:AbortSignal.timeout(2000),redirect:'error'})
      if(response.status!==200)return false
      return typeof (await response.json()).token==='string'
    },'APPLICATION_NOT_READY',120)
    assert.equal(maintenance('status').mode,'RUNNING')
    if(receipt?.project===runtime.project&&receipt.status==='STOPPED'){
      receipt.status='STARTED';receipt.startedAt=new Date().toISOString();await save()
    }
    report.url=runtime.url;report.adminUrl=new URL('/admin.html',origin).href;report.gate='RUNNING'
    console.log('Web san sang: '+report.url+' | Admin: '+report.adminUrl)
  } else {
    assert(db.State.Running,'DATABASE_STOPPED: khong thay gate khi chua doi chieu')
    const guard=maintenance('status')
    if(!app.State.Running&&mayResume(runtime.project,guard,receipt)) {
      docker(['stop',runtime.database])
    } else {
      assert(app.State.Running&&guard.mode==='RUNNING','MAINTENANCE_OR_STOPPED: doi chieu truoc khi dung')
      receipt={project:runtime.project,owner:randomUUID(),status:'STOPPING',startedAt:new Date().toISOString()};await save()
      maintenance('quiesce',receipt.owner)
      await waitFor(()=>{
        const state=maintenance('status');assert.equal(state.owner,receipt.owner)
        return ['activities','tasks','claims','deleting'].every(k=>state[k]===0)
      },'DRAIN_INCOMPLETE_KEEP_CLOSED',60)
      maintenance('freeze',receipt.owner)
      receipt.status='STOPPED';receipt.stoppedAt=new Date().toISOString();await save()
      docker(['stop',runtime.database])
    }
    report.gate='FROZEN';report.databaseRunning=false;report.applicationRunning=false
    console.log('Da dung an toan. Database/audio va khoa duoc giu nguyen.')
  }
  report.status='SUCCESS';report.finishedAt=new Date().toISOString()
  await writeFile(join(directory,'quick-run-last-'+action+'.json'),JSON.stringify(report,null,2))
  console.log(JSON.stringify(report))
}

if(process.argv[1]&&resolve(process.argv[1])===fileURLToPath(import.meta.url)) {
  main().catch(error=>{console.error('Khong hoan tat: '+(error.code==='ERR_ASSERTION'?error.message:error.code||error.name)+'. Giu nguyen du lieu/gate, doi chieu truoc khi thu lai.');process.exitCode=1})
}
