import assert from 'node:assert/strict'
import {execFileSync} from 'node:child_process'
import {readFile,writeFile,mkdir} from 'node:fs/promises'
import {join,resolve} from 'node:path'
import {plainDirectory,disjoint} from '../../../../../deploy/operations-retention.mjs'

const [restoredArg,sourceArg,evidenceArg]=process.argv.slice(2)
assert(restoredArg&&sourceArg&&evidenceArg,'Usage: prepare-acceptance.mjs restored-private-runtime serving-credential-runtime new-evidence-directory')
const directory=await plainDirectory(restoredArg),source=await plainDirectory(sourceArg),evidence=resolve(evidenceArg)
for(const path of [directory,source,process.cwd()])disjoint(path,evidence)
const runtime=JSON.parse(await readFile(join(directory,'runtime.json'),'utf8'))
const origin=JSON.parse(await readFile(join(source,'runtime.json'),'utf8'))
const proof=JSON.parse(await readFile(join(directory,'restore-result.json'),'utf8'))
assert.match(runtime.project,/^hueqr-ops-check-[a-f0-9]{8}$/)
assert.notEqual(runtime.project,origin.project);assert.notEqual(runtime.serviceEpoch,origin.serviceEpoch)
assert.equal(proof.status,'RESTORE_VERIFIED_CLOSED')
const docker=(args,input)=>execFileSync('docker',args,{input,encoding:'utf8',windowsHide:true,timeout:120000,maxBuffer:4*1024*1024,stdio:['pipe','pipe','pipe']}).trim()
const app=JSON.parse(docker(['inspect',runtime.application]))[0],db=JSON.parse(docker(['inspect',runtime.database]))[0]
assert(!app.State.Running&&!db.State.Running)
assert.equal(app.Config.Labels['com.docker.compose.project'],runtime.project)
assert.equal(db.Config.Labels['com.docker.compose.project'],runtime.project)
const env=Object.fromEntries(app.Config.Env.map(s=>{const i=s.indexOf('=');return[s.slice(0,i),s.slice(i+1)]}))
for(const key of ['HUEQR_TTS_ENABLED','HUEQR_TTS_LOCAL_ENABLED','HUEQR_CLEANUP_ENABLED','HUEQR_RETENTION_ENABLED'])assert.equal(env[key],'false')
await mkdir(evidence)
const sid=execFileSync('powershell.exe',['-NoProfile','-Command','[Security.Principal.WindowsIdentity]::GetCurrent().User.Value'],{encoding:'utf8',windowsHide:true,stdio:'pipe'}).trim()
assert.match(sid,/^S-1-[0-9-]+$/)
execFileSync('icacls.exe',[evidence,'/inheritance:r','/grant:r',`*${sid}:(OI)(CI)F`,'*S-1-5-18:(OI)(CI)F'],{stdio:'pipe',windowsHide:true})
docker(['start',runtime.database])
const sql=q=>docker(['exec','-i',runtime.database,'sh','-c','MYSQL_PWD="$MYSQL_PASSWORD" exec mysql -u hueqr -N -B hueqr'],q)
let ready=false
for(let i=0;i<90;i++){try{ready=sql('SELECT 1;')==='1'}catch{};if(ready)break;await new Promise(r=>setTimeout(r,1000))}
assert(ready)
assert.equal(sql('SELECT mode FROM ops_guard WHERE id=1;'),'FROZEN')
execFileSync(process.execPath,['deploy/maintenance.mjs',directory,'resume',runtime.maintenanceToken],{encoding:'utf8',windowsHide:true,timeout:300000,stdio:'pipe'})
await writeFile(join(evidence,'private-runtime.json'),JSON.stringify({...runtime,adminLogin:origin.adminLogin,adminPassword:origin.adminPassword,public:false,fixtureSessionPrefix:'Acceptance-'},null,2),{flag:'wx'})
await writeFile(join(evidence,'preparation.json'),JSON.stringify({preparedAt:new Date().toISOString(),status:'PRIVATE_ACCEPTANCE_READY',project:runtime.project,public:false,allWorkersDisabled:true,sourceServingModified:false},null,2),{flag:'wx'})
console.log(JSON.stringify({status:'PRIVATE_ACCEPTANCE_READY',project:runtime.project,evidence}))
