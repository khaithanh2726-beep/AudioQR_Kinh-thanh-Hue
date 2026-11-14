import assert from 'node:assert/strict'
import {readFile,writeFile} from 'node:fs/promises'
import {execFileSync} from 'node:child_process'
import {join,resolve} from 'node:path'
import {pathToFileURL} from 'node:url'
import {randomUUID} from 'node:crypto'
import {release} from 'node:os'

const directory=resolve(process.argv[2]||'')
const runtime=JSON.parse(await readFile(join(directory,'private-runtime.json'),'utf8'))
assert(runtime.public===false&&/^hueqr-ops-check-[a-f0-9]{8}$/.test(runtime.project))
const origin=new URL(runtime.url)
assert(origin.protocol==='http:'&&['localhost','127.0.0.1'].includes(origin.hostname))
const {chromium,expect}=await import(pathToFileURL(resolve('frontend/node_modules/@playwright/test/index.mjs')))
const {interceptTiles}=await import(pathToFileURL(resolve('frontend/tests/e2e/helpers/tile-fixture.mjs')))
const docker=(args,input)=>execFileSync('docker',args,{input,encoding:'utf8',windowsHide:true,timeout:30000,stdio:['pipe','pipe','pipe']}).trim()
const sql=q=>docker(['exec','-i',runtime.database,'sh','-c','MYSQL_PWD="$MYSQL_PASSWORD" exec mysql -u hueqr -N -B hueqr'],q)
for(const name of [runtime.application,runtime.database])assert.equal(JSON.parse(docker(['inspect',name]))[0].Config.Labels['com.docker.compose.project'],runtime.project)
let ready=false
for(let i=0;i<120;i++){
 try{ready=(await fetch(new URL('/api/v1/auth/csrf',origin),{signal:AbortSignal.timeout(2000)})).status===200}catch{}
 if(ready)break
 await new Promise(done=>setTimeout(done,1000))
}
assert(ready,'Private restored application must be ready before browser scenarios')
const report={startedAt:new Date().toISOString(),status:'RUNNING',project:runtime.project,realApi:true,
 tileFixture:true,physicalDevice:false,fixtureClockOrNetworkFaultsAreExplicit:true,providerRequests:0,cases:[]}
const browser=await chromium.launch({headless:true,executablePath:process.env.HUEQR_BROWSER_PATH||'C:/Program Files/Google/Chrome/Application/chrome.exe'})
Object.assign(report,{browser:'Chrome',browserVersion:browser.version(),platform:process.platform,osRelease:release(),nodeVersion:process.version})
const sessions=new Map()
async function context(){
 const c=await browser.newContext({baseURL:runtime.url,viewport:{width:1280,height:800}})
 c.setDefaultTimeout(15000)
 await c.route('**/*',route=>{
  const url=new URL(route.request().url())
  return url.origin===origin.origin||['blob:','data:'].includes(url.protocol)?route.continue():route.abort()
 })
 return c
}
async function page(c){const p=await c.newPage();await interceptTiles(p);return p}
async function saved(p){return p.evaluate(()=>JSON.parse(sessionStorage.getItem('hueqr-guest-tab-v1')))}
async function remember(p){const s=await saved(p);assert.match(s.sessionId,/^[0-9a-f-]{36}$/);sessions.set(s.sessionId,s.credential);return s}
async function start(p,name='Acceptance-'+randomUUID()){
 await p.goto('/');await p.locator('input[name="displayName"]').fill(name)
 await p.getByRole('button',{name:'Bắt đầu tham quan',exact:true}).click()
 await expect(p.locator('.map-canvas')).toBeVisible();return remember(p)
}
async function select(p){await p.locator('.map-canvas').click();await expect(p.locator('.text-body')).not.toHaveText('');await expect(p.locator('audio')).toHaveAttribute('src',/^blob:/)}
const views=id=>Number(sql(`SELECT COUNT(*) FROM place_view WHERE session_id=UUID_TO_BIN('${id}');`))
async function scenario(id,run){
 const c=await context(),result={id,status:'RUNNING'}
 try{await run(c,result);result.status='PASSED'}
 catch(error){result.status='FAILED';result.errorName=error.name;process.exitCode=1;try{const pages=c.pages();if(pages[0])await pages[0].screenshot({path:join(directory,id+'-failure.png'),fullPage:true})}catch{}}
 finally{await c.close();report.cases.push(result);await writeFile(join(directory,'browser-acceptance.json'),JSON.stringify(report,null,2));console.log(JSON.stringify(result))}
}
async function login(p){
 await p.goto('/admin.html');await p.locator('#login-name').fill(runtime.adminLogin);await p.locator('#login-password').fill(runtime.adminPassword)
 await p.getByRole('button',{name:'Đăng nhập',exact:true}).click();await expect(p.locator('.admin-place')).toHaveCount(5)
}
const administrativeWrites=p=>{
 const writes=[];p.on('request',r=>{const path=new URL(r.url()).pathname;if(r.method()!=='GET'&&path.startsWith('/api/v1/admin/')&&!path.endsWith('/session/activity'))writes.push(path)});return writes
}
try{
 await scenario('tabs-reload',async(c,r)=>{
  const a=await page(c),b=await page(c),name='Acceptance-'+randomUUID()
  const sa=await start(a,name),sb=await start(b,name);assert.notEqual(sa.sessionId,sb.sessionId);assert.notEqual(sa.credential,sb.credential)
  await select(a);await expect.poll(()=>views(sa.sessionId)).toBe(1)
  await a.locator('audio').evaluate(el=>{el.pause();el.currentTime=4})
  await a.reload();await expect(a.locator('audio')).toHaveAttribute('src',/^blob:/)
  const restored=await remember(a);assert.equal(restored.sessionId,sa.sessionId)
  assert.equal(await a.locator('audio').evaluate(el=>el.paused),true);assert.equal(views(sa.sessionId),1)
  await a.getByRole('button',{name:'Kết thúc tham quan',exact:true}).click()
  await expect(a.getByRole('heading',{name:'Đã kết thúc tham quan',exact:true})).toBeVisible()
  await b.reload();await expect(b.locator('.map-canvas')).toBeVisible();assert.equal((await remember(b)).sessionId,sb.sessionId)
  Object.assign(r,{independentSessions:true,reloadSameSession:true,reloadPaused:true,viewsAfterReload:1,otherTabStillActive:true})
 })
 await scenario('duplicate-owner',async(c,r)=>{
  const a=await page(c),original=await start(a)
  const popup=c.waitForEvent('page');await a.evaluate(()=>{window.open('/','_blank')});const b=await popup;await interceptTiles(b)
  await expect(b.locator('input[name="displayName"]')).toBeVisible()
  const cloned=await saved(b);assert.notEqual(cloned.ownerId,original.ownerId);assert.equal(cloned.sessionId,undefined)
  assert.equal((await saved(a)).sessionId,original.sessionId);r.clonedStorageCannotStealSession=true
 })
 await scenario('lost-start-response',async(c,r)=>{
  const p=await page(c);let first=true;const ids=[]
  await p.route('**/api/v1/guest/sessions',async route=>{
   if(route.request().method()!=='POST')return route.continue()
   ids.push(route.request().headers()['idempotency-key'])
   const actual=await route.fetch()
   if(first){first=false;await actual.dispose();return route.abort('connectionreset')}
   return route.fulfill({response:actual})
  })
  await p.goto('/');const name='Acceptance-'+randomUUID();await p.locator('input[name="displayName"]').fill(name)
  await p.getByRole('button',{name:'Bắt đầu tham quan',exact:true}).click()
  await expect(p.getByRole('button',{name:'Thử lại',exact:true})).toBeVisible()
  await p.getByRole('button',{name:'Thử lại',exact:true}).click();await expect(p.locator('.map-canvas')).toBeVisible();await remember(p)
  assert.equal(new Set(ids).size,1);assert(ids.length>=2)
  assert.equal(sql(`SELECT COUNT(*) FROM guest_session WHERE display_name='${name}';`),'1')
  Object.assign(r,{responseDroppedAfterCommit:true,sameKeyReused:true,sessionRows:1})
 })
 await scenario('lost-selection-response',async(c,r)=>{
  const p=await page(c),s=await start(p);let first=true;const keys=[]
  await p.route('**/openings',async route=>{
   keys.push(route.request().headers()['idempotency-key']);const actual=await route.fetch()
   if(first){first=false;await actual.dispose();return route.abort('connectionreset')}
   return route.fulfill({response:actual})
  })
  await p.locator('.map-canvas').click();await expect(p.getByRole('button',{name:'Thử lại',exact:true})).toBeVisible()
  assert.equal(views(s.sessionId),0)
  await p.getByRole('button',{name:'Thử lại',exact:true}).click();await expect(p.locator('audio')).toHaveAttribute('src',/^blob:/)
  await expect.poll(()=>views(s.sessionId)).toBe(1);assert.equal(new Set(keys).size,1)
  Object.assign(r,{responseDroppedAfterCommit:true,sameKeyReused:true,viewsBeforeRetry:0,viewsAfterRetry:1})
 })
 await scenario('audio-error-retry',async(c,r)=>{
  const p=await page(c),s=await start(p);let fail=true
  await p.route('**/content-grants/*/audio',route=>fail?route.abort('connectionreset'):route.continue())
  await p.locator('.map-canvas').click();await expect(p.getByText('Chưa phát được audio. Bạn có thể thử lại khi phiên còn hiệu lực',{exact:true})).toBeVisible()
  await expect(p.locator('.text-body')).not.toHaveText('');await expect.poll(()=>views(s.sessionId)).toBe(1)
  fail=false;await p.getByRole('button',{name:'Thử lại',exact:true}).click();await expect(p.locator('audio')).toHaveAttribute('src',/^blob:/)
  assert.equal(views(s.sessionId),1);r.textPreserved=true;r.retryDoesNotAddView=true
 })
 await scenario('offline-reload-recovery',async(c,r)=>{
  const p=await page(c),s=await start(p);await select(p);await expect.poll(()=>views(s.sessionId)).toBe(1)
  let offline=true;await p.route('**/api/v1/guest/**',route=>offline?route.abort('internetdisconnected'):route.continue())
  await p.reload();await expect(p.getByRole('button',{name:'Bắt đầu phiên mới',exact:true})).toBeVisible()
  assert.equal((await saved(p)).sessionId,s.sessionId)
  offline=false;await p.reload();await expect(p.locator('audio')).toHaveAttribute('src',/^blob:/)
  assert.equal((await remember(p)).sessionId,s.sessionId);assert.equal(views(s.sessionId),1)
  Object.assign(r,{apiNetworkFault:true,credentialsPreserved:true,recoveredSameSession:true,views:1})
 })
 await scenario('expiry-warning-tail',async(c,r)=>{
  const p=await page(c);await p.clock.install();const s=await start(p);await select(p)
  assert.equal(sql(`SELECT COUNT(*) FROM guest_session WHERE id=UUID_TO_BIN('${s.sessionId}') AND display_name LIKE 'Acceptance-%';`),'1')
  // Chỉ sửa timestamp của phiên fixture mới trên bản restore riêng; quan hệ 8 giờ giữ nguyên.
  sql(`UPDATE guest_session SET started_at=UTC_TIMESTAMP(6)-INTERVAL 8 HOUR+INTERVAL 4 MINUTE,expires_at=UTC_TIMESTAMP(6)+INTERVAL 4 MINUTE WHERE id=UUID_TO_BIN('${s.sessionId}') AND display_name LIKE 'Acceptance-%';`)
  await p.reload();await expect(p.locator('.warning')).toBeVisible();await expect(p.locator('audio')).toHaveAttribute('src',/^blob:/)
  await p.locator('audio').evaluate(el=>el.play());await expect.poll(()=>p.locator('audio').evaluate(el=>!el.paused)).toBe(true)
  sql(`UPDATE guest_session SET started_at=UTC_TIMESTAMP(6)-INTERVAL 8 HOUR-INTERVAL 1 SECOND,expires_at=UTC_TIMESTAMP(6)-INTERVAL 1 SECOND WHERE id=UUID_TO_BIN('${s.sessionId}') AND display_name LIKE 'Acceptance-%';`)
  await p.clock.fastForward(241000)
  await expect(p.getByRole('heading',{name:'Phiên tham quan đã hết hạn',exact:true})).toBeVisible()
  await expect(p.locator('.seek')).toBeDisabled();await expect(p.locator('.language-field select')).toBeDisabled()
  await expect(p.locator('.text-body')).not.toHaveText('');await expect(p.locator('.play-button')).toBeEnabled()
  await p.locator('.play-button').click();await expect.poll(()=>p.locator('audio').evaluate(el=>el.paused)).toBe(true)
  await p.locator('.play-button').click();await expect.poll(()=>p.locator('audio').evaluate(el=>!el.paused)).toBe(true)
  await p.reload();await expect(p.getByRole('heading',{name:'Phiên tham quan đã hết hạn',exact:true})).toBeVisible()
  assert.equal(await p.locator('audio').getAttribute('src'),null)
  Object.assign(r,{ownSessionTimestampFixture:true,browserClockFixture:true,waitedEightRealHours:false,warningShown:true,tailPauseResume:true,reloadCannotRestoreTail:true})
 })
 await scenario('admin-q2-reload',async(c,r)=>{
  const p=await page(c),writes=administrativeWrites(p);await login(p)
  await p.locator('.admin-place').filter({hasText:'Cổng Ngọ Môn'}).getByRole('button',{name:'Nội dung và cặp',exact:true}).click()
  const original=await p.locator('#text-body').inputValue(),draft=original+'\nBản kiểm chưa lưu.'
  await p.locator('#text-body').fill(draft);await p.getByRole('button',{name:'Về danh sách',exact:true}).click()
  await p.getByRole('button',{name:'Quay lại soạn',exact:true}).click();await expect(p.locator('#text-body')).toHaveValue(draft)
  p.on('dialog',dialog=>dialog.accept());await p.reload();await expect(p.locator('#text-body')).toHaveValue(draft)
  assert.deepEqual(writes,[]);r.unsavedTextPreserved=true;r.administrativeWrites=0
 })
 await scenario('admin-401-q2',async(c,r)=>{
  const p=await page(c),writes=administrativeWrites(p);await login(p)
  await p.locator('.admin-place').filter({hasText:'Cổng Ngọ Môn'}).getByRole('button',{name:'Nội dung và cặp',exact:true}).click()
  const draft=(await p.locator('#text-body').inputValue())+'\nBản kiểm mất quyền.';await p.locator('#text-body').fill(draft)
  await p.route('**/api/v1/admin/places/**',route=>route.fulfill({status:401,json:{code:'ADMIN_SESSION_EXPIRED'}}))
  await p.getByRole('button',{name:'Đọc lại trạng thái',exact:true}).click()
  await expect(p.getByRole('heading',{name:'Đăng nhập lại để tiếp tục',exact:true})).toBeVisible();await expect(p.locator('#text-body')).toHaveCount(0)
  await p.unroute('**/api/v1/admin/places/**')
  await p.locator('#login-name').fill(runtime.adminLogin);await p.locator('#login-password').fill(runtime.adminPassword)
  await p.getByRole('button',{name:'Đăng nhập',exact:true}).click();await expect(p.locator('#text-body')).toHaveValue(draft)
  assert.deepEqual(writes,[]);Object.assign(r,{http401Fixture:true,waitedThirtyRealMinutes:false,editorHidden:true,restoredSameAccountDraft:true,administrativeWrites:0})
 })
 await scenario('admin-statistics-filters',async(c,r)=>{
  const p=await page(c);await login(p)
  await p.getByRole('button',{name:'Phiên và thống kê',exact:true}).click();await p.getByRole('button',{name:'Thống kê',exact:true}).click()
  await p.getByRole('button',{name:'Xem dữ liệu',exact:true}).click();await expect(p.getByRole('heading',{name:/Phiên bắt đầu trong khoảng/})).toBeVisible()
  const from=p.getByLabel('Từ ngày',{exact:true}),date=new Date((await from.inputValue())+'T00:00:00Z');date.setUTCDate(date.getUTCDate()-1)
  await from.fill(date.toISOString().slice(0,10));await expect(p.getByText('Bộ lọc đã thay đổi. Bảng dưới thuộc lần tải trước.',{exact:true})).toBeVisible()
  r.realStatisticsRead=true;r.oldResultsLabelled=true
 })
 await scenario('crash-guard-unknown-lease',async(c,r)=>{
  const fixture=randomUUID(),instance=randomUUID(),owner=randomUUID()
  const maintain=action=>execFileSync(process.execPath,['deploy/maintenance.mjs',runtime.configPath.replace(/[\\/][^\\/]+$/,''),action,owner],{
   encoding:'utf8',windowsHide:true,timeout:180000,stdio:'pipe',maxBuffer:4*1024*1024})
  await writeFile(join(directory,'crash-fixture-private-'+fixture+'.json'),JSON.stringify({project:runtime.project,fixture,instance,owner},null,2),{flag:'wx'})
  sql(`INSERT INTO ops_activity(id,instance_id,kind,started_at) VALUES('${fixture}','${instance}','HTTP','2000-01-01');`)
  maintain('quiesce')
  docker(['kill',runtime.application]);docker(['start',runtime.application])
  let closed=false
  for(let i=0;i<120;i++){
   try{closed=(await fetch(new URL('/api/v1/auth/csrf',origin),{signal:AbortSignal.timeout(2000)})).status===503}catch{}
   if(closed)break
   await new Promise(done=>setTimeout(done,1000))
  }
  assert(closed);assert.equal(sql('SELECT mode FROM ops_guard WHERE id=1;'),'QUIESCING')
  assert.equal(sql(`SELECT COUNT(*) FROM ops_activity WHERE id='${fixture}' AND instance_id='${instance}';`),'1')
  assert.throws(()=>maintain('freeze'))
  assert.equal(JSON.parse(docker(['inspect',runtime.application]))[0].State.Running,true)
  // Chỉ tháo đúng lease fixture do ca này tạo, không đưa thành cơ chế thu hồi lease thật.
  sql(`DELETE FROM ops_activity WHERE id='${fixture}' AND instance_id='${instance}' AND kind='HTTP' AND started_at='2000-01-01';`)
  const app=JSON.parse(docker(['inspect',runtime.application]))[0]
  const env=Object.fromEntries(app.Config.Env.map(s=>{const i=s.indexOf('=');return[s.slice(0,i),s.slice(i+1)]}))
  assert.equal(env.HUEQR_SERVICE_EPOCH,runtime.serviceEpoch);assert.equal(env.HUEQR_GUEST_SIGNING_KEY_BASE64,runtime.signingKeyBase64)
  maintain('resume');assert.equal((await fetch(new URL('/api/v1/auth/csrf',origin))).status,200)
  Object.assign(r,{actualPrivateContainerKilled:true,gateSurvivesRestart:true,unknownOldLeaseNotReaped:true,freezeRefused:true,
   onlyOwnFixtureDeleted:true,keysAndEpochPreserved:true,doesNotProveAllCrashReconciliation:true})
 })
}finally{
 for(const[id,credential]of sessions){try{await fetch(new URL(`/api/v1/guest/sessions/${id}/end`,origin),{method:'POST',headers:{'X-Guest-Credential':credential,'Idempotency-Key':randomUUID()},signal:AbortSignal.timeout(5000)})}catch{}}
 await browser.close()
 report.finishedAt=new Date().toISOString();report.passed=report.cases.filter(c=>c.status==='PASSED').length;report.failed=report.cases.length-report.passed
 report.status=report.failed?'FAILED':'PASSED'
 await writeFile(join(directory,'browser-acceptance.json'),JSON.stringify(report,null,2))
 console.log(JSON.stringify({status:report.status,passed:report.passed,failed:report.failed,physicalDevice:false}))
}
