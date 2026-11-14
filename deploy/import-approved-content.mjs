import assert from 'node:assert/strict'
import {execFileSync} from 'node:child_process'
import {mkdir,readFile,writeFile} from 'node:fs/promises'
import {join,resolve} from 'node:path'
import {createHash} from 'node:crypto'
import {plainDirectory,disjoint} from './operations-retention.mjs'
import {verifyOperationsBackup} from './operations-backup.mjs'

// Nhập dữ liệu bất biến đã tạo thật; hồ sơ duyệt/Apply/hiển thị vẫn qua Service của API Admin.
const [sourceArg,sourceBackupArg,targetArg,owner,targetBackupArg,outputArg]=process.argv.slice(2)
assert(outputArg,'Usage: import-approved-content.mjs source-runtime source-backup target-runtime owner target-backup new-private-report')
assert.match(owner||'',/^[0-9a-f-]{36}$/)
const sourceRoot=await plainDirectory(sourceArg),targetRoot=await plainDirectory(targetArg)
disjoint(sourceRoot,targetRoot)
const output=resolve(outputArg)
for(const path of [process.cwd(),sourceRoot,targetRoot])disjoint(path,output)
const json=async file=>JSON.parse((await readFile(file,'utf8')).replace(/^\uFEFF/,''))
const source=await json(join(sourceRoot,'runtime.json')),target=await json(join(targetRoot,'runtime.json'))
assert.match(source.project,/^hueqr-ops-check-[a-f0-9]{8}$/)
assert.match(target.project,/^hueqr-(ops-check|rehearsal)-[a-f0-9]{8}$/)
assert.notEqual(source.project,target.project)
const approval=await json('docs/05-noi-dung/duyet-19-cap-v02-20261114.json')
assert.equal(approval.status,'APPROVED_FOR_PUBLICATION');assert.equal(approval.articles.length,19)
const hash=bytes=>createHash('sha256').update(bytes).digest('hex')
assert.equal(hash(await readFile('docs/05-noi-dung/demo-content-v02.json')),approval.sourceBundleSha256)
const sourceBackup=await plainDirectory(sourceBackupArg),targetBackup=await plainDirectory(targetBackupArg)
const sourceManifest=await json(join(sourceBackup,'manifest.json')),targetManifest=await json(join(targetBackup,'manifest.json'))
await verifyOperationsBackup(sourceBackup,sourceManifest);await verifyOperationsBackup(targetBackup,targetManifest)
assert.equal(targetManifest.checkpoint,owner)
const docker=(args,input)=>execFileSync('docker',args,{input,encoding:'utf8',windowsHide:true,timeout:180000,maxBuffer:128*1024*1024,stdio:['pipe','pipe','pipe']}).trim()
const inspect=name=>JSON.parse(docker(['inspect',name]))[0]
const sql=(r,q)=>docker(['exec','-i',r.database,'sh','-c','MYSQL_PWD="$MYSQL_PASSWORD" exec mysql --default-character-set=utf8mb4 -u hueqr -N -B hueqr'],q)
const ids=values=>values.map(id=>{assert.match(id,/^[0-9a-f-]{36}$/);return '0x'+id.replaceAll('-','')}).join(',')
const textIds=ids(approval.articles.map(a=>a.textRevisionId)),audioIds=ids(approval.articles.map(a=>a.audioRevisionId))
const predicates={runtime_instance:`id IN (SELECT conversion_instance FROM tts_job WHERE target_text_revision_id IN (${textIds}) AND conversion_instance IS NOT NULL)`,file_asset:`id IN (SELECT wav_file_id FROM audio_revision WHERE id IN (${audioIds}) UNION SELECT mp3_file_id FROM audio_revision WHERE id IN (${audioIds}))`,
 text_revision:`id IN (${textIds})`,tts_job:`target_text_revision_id IN (${textIds})`,audio_revision:`id IN (${audioIds})`}
const tables=Object.keys(predicates)
function stopped(r){
 const app=inspect(r.application),db=inspect(r.database)
 for(const c of [app,db])assert.equal(c.Config.Labels['com.docker.compose.project'],r.project)
 assert(!app.State.Running&&db.State.Running,'Both applications stopped, databases running')
 assert.equal(db.HostConfig.PortBindings?.['3306/tcp'],undefined)
 assert.equal(sql(r,'SELECT mode FROM ops_guard WHERE id=1;'),'FROZEN')
 assert.equal(sql(r,'SELECT (SELECT COUNT(*) FROM ops_activity)+(SELECT COUNT(*) FROM runtime_task)+(SELECT COUNT(*) FROM file_claim);'),'0')
 assert.equal(sql(r,'SELECT COUNT(*) FROM information_schema.processlist WHERE ID<>CONNECTION_ID() AND DB=DATABASE();'),'0')
 return app
}
function dump(r,table,where='1'){
 assert.match(table,/^[a-z_]+$/)
 const raw=docker(['exec','-e','HUEQR_WHERE='+where,r.database,'sh','-c',
  'MYSQL_PWD="$MYSQL_PASSWORD" exec mysqldump -u hueqr --default-character-set=utf8mb4 --no-create-info --skip-lock-tables --compact --skip-triggers --skip-extended-insert --complete-insert --hex-blob --order-by-primary --no-tablespaces --set-gtid-purged=OFF hueqr '+table+' --where "$HUEQR_WHERE"'])
 const lines=raw.split('\n').filter(l=>l.startsWith('INSERT INTO `'+table+'` '))
 assert(lines.every(l=>l.endsWith(';')),'Unexpected multiline dump')
 assert.equal(lines.length,Number(sql(r,`SELECT COUNT(*) FROM ${table} WHERE ${where};`)))
 return lines.join('\n')
}
await mkdir(output)
const sid=execFileSync('powershell.exe',['-NoProfile','-Command','[Security.Principal.WindowsIdentity]::GetCurrent().User.Value'],{encoding:'utf8',windowsHide:true,stdio:'pipe'}).trim()
assert.match(sid,/^S-1-[0-9-]+$/)
execFileSync('icacls.exe',[output,'/inheritance:r','/grant:r',`*${sid}:(OI)(CI)F`,'*S-1-5-18:(OI)(CI)F'],{windowsHide:true,stdio:'pipe'})
const report={startedAt:new Date().toISOString(),status:'RUNNING',source:source.project,target:target.project,providerRequests:0,filesCopied:0}
const locks=[]
try{
 stopped(source);const app=stopped(target)
 assert.equal(app.Image,targetManifest.sourceImage);assert.equal(inspect(source.application).Image,sourceManifest.sourceImage)
 assert.equal(sql(target,`SELECT COUNT(*) FROM ops_guard WHERE mode='FROZEN' AND owner_token='${owner}';`),'1')
 for(const r of [source,target]){docker(['exec',r.database,'mkdir','/tmp/hueqr-operations-lock']);locks.push(r)}
 assert.equal(sql(source,`SELECT COUNT(*) FROM text_revision WHERE id IN (${textIds}) AND active_tts_job_id IS NOT NULL;`),'0')
 assert.equal(sql(source,`SELECT COUNT(*) FROM text_revision WHERE id IN (${textIds}) AND LOWER(HEX(content_hash))=SHA2(body,256);`),'19')
 assert.equal(sql(source,`SELECT COUNT(*) FROM audio_revision a JOIN tts_job j ON j.id=a.job_id WHERE a.id IN (${audioIds}) AND j.phase='SUCCEEDED' AND j.model='gemini-3.8-flash-tts' AND j.voice='Charon';`),'19')
 assert.equal(sql(source,`SELECT COUNT(*) FROM tts_job WHERE ${predicates.tts_job} AND phase NOT IN ('SUCCEEDED','FAILED','DISCARDED');`),'0')
 assert.equal(sql(source,`SELECT COUNT(*) FROM runtime_instance WHERE ${predicates.runtime_instance} AND state<>'STOPPED';`),'0','Historical conversion instances must be stopped')
 const sourceSlots=sql(source,`SELECT LOWER(HEX(s.id)),LOWER(HEX(s.place_id)),s.language,LOWER(HEX(t.id)),LOWER(HEX(t.content_hash)) FROM content_slot s JOIN text_revision t ON t.slot_id=s.id WHERE t.id IN (${textIds}) ORDER BY s.id;`)
 const mappings=sourceSlots.split('\n').map(l=>l.split('\t'));assert.equal(mappings.length,19)
 for(const [slot,place,language,text,checksum]of mappings){
  assert(approval.articles.some(a=>a.textRevisionId.replaceAll('-','')===text&&a.language===language&&a.textSha256===checksum))
  assert.equal(sql(target,`SELECT COUNT(*) FROM content_slot WHERE id=0x${slot} AND place_id=0x${place} AND language='${language}' AND draft_text_id IS NULL AND active_pair_id IS NULL;`),'1','Target has edits; do not overwrite')
 }
 const inserts=[]
 for(const table of tables){
  assert.equal(sql(source,`SHOW CREATE TABLE ${table};`),sql(target,`SHOW CREATE TABLE ${table};`),'Schema mismatch')
  const where=predicates[table]
  // Chỉ dùng điều kiện file dựa trên tập ID nguồn cụ thể, không dựa vào bảng audio đích còn trống.
  if(table==='file_asset'){
   const hex=sql(source,`SELECT LOWER(HEX(id)) FROM file_asset WHERE ${where} ORDER BY id;`).split('\n')
   assert.equal(hex.length,38);predicates.file_asset='id IN ('+hex.map(x=>{assert.match(x,/^[0-9a-f]{32}$/);return '0x'+x}).join(',')+')'
  }
  const selected=predicates[table]
  assert.equal(sql(target,`SELECT COUNT(*) FROM ${table} WHERE ${selected};`),'0','Existing import requires reconciliation')
  const statement=dump(source,table,selected);inserts.push(statement)
 }
 const protectedTables=sql(target,'SHOW TABLES;').split('\n').filter(t=>!tables.includes(t)&&t!=='content_slot')
 const protectedDump=()=>protectedTables.map(t=>dump(target,t)).join('\n')+'\n'+tables.map(t=>dump(target,t,`NOT (${predicates[t]})`)).join('\n')
 const preservedBefore=hash(protectedDump())
 await writeFile(join(output,'before.json'),JSON.stringify({protectedDataSha256:preservedBefore,mappings:sourceSlots},null,2),{flag:'wx'})
 const sourceFiles=sql(source,`SELECT storage_key,LOWER(HEX(checksum)),byte_size FROM file_asset WHERE ${predicates.file_asset} AND state='READY' ORDER BY storage_key;`).split('\n').map(l=>l.split('\t'))
 assert.equal(sourceFiles.length,38)
 for(const [key,checksum,size]of sourceFiles){
  const f=sourceManifest.files.find(f=>f.storageKey===key);assert(f&&f.sha256===checksum&&f.bytes===Number(size))
  assert.match(key,/^[0-9a-f-]{36}\.(wav|mp3)$/)
  assert.equal(sql(target,`SELECT COUNT(*) FROM file_asset WHERE storage_key='${key}';`),'0')
  try{docker(['cp',target.application+':/data/audio/'+key,join(output,'existing-'+key)]);assert.fail('Refuse overwriting a physical file')}
  catch(e){if(e.code==='ERR_ASSERTION')throw e;assert.match(String(e.stderr),/Could not find the file|no such file|not found/i)}
  docker(['cp',join(sourceBackup,'audio',key),target.application+':/data/audio/'+key])
  // Đọc lại từ volume; không coi Hash ở thư mục backup là Hash của đích.
  const copied=join(output,key);docker(['cp',target.application+':/data/audio/'+key,copied])
  assert.equal(hash(await readFile(copied)),checksum);report.filesCopied++
 }
 const slotIds=mappings.map(m=>'0x'+m[0]).join(',')
 const guard=`CREATE TEMPORARY TABLE hueqr_import_assert (ok INT NOT NULL CHECK(ok=1));\nSTART TRANSACTION;\nSELECT id FROM content_slot WHERE id IN (${slotIds}) ORDER BY id FOR UPDATE;\nINSERT INTO hueqr_import_assert SELECT IF((SELECT COUNT(*) FROM content_slot WHERE id IN (${slotIds}) AND draft_text_id IS NULL AND active_pair_id IS NULL)=19 AND @@foreign_key_checks=1,1,0);\n`
 const update=`UPDATE content_slot s JOIN text_revision t ON t.slot_id=s.id SET s.draft_text_id=t.id,s.version=s.version+1 WHERE t.id IN (${textIds}) AND s.draft_text_id IS NULL AND s.active_pair_id IS NULL;\nINSERT INTO hueqr_import_assert VALUES(IF(ROW_COUNT()=19,1,0));\n`
 const batch=guard+inserts.join('\n')+'\n'+update+'COMMIT;\nSELECT \'CONTENT_IMPORT_COMMITTED\';'
 await writeFile(join(output,'import-private.sql'),batch,{flag:'wx'})
 report.status='IMPORT_DISPATCHED';await writeFile(join(output,'result.json'),JSON.stringify(report,null,2))
 const result=sql(target,batch);assert(result.endsWith('CONTENT_IMPORT_COMMITTED'))
 assert.equal(hash(protectedDump()),preservedBefore,'Existing user/history/configuration data changed')
 assert.equal(sql(target,`SELECT COUNT(*) FROM audio_revision WHERE ${predicates.audio_revision};`),'19')
 Object.assign(report,{status:'IMPORTED_FROZEN',textRevisions:19,audioRevisions:19,existingDataPreserved:true,foreignKeysKeptEnabled:true,profilesOrPairsApplied:false,gate:'FROZEN'})
}catch(error){await writeFile(join(output,'failure-private.txt'),String(error.stack));report.status=report.status==='IMPORT_DISPATCHED'?'IMPORT_OUTCOME_REQUIRES_RECONCILIATION':'FAILED_CLOSED';report.errorName=error.name;process.exitCode=1}
finally{
 for(const r of locks.reverse())docker(['exec',r.database,'rmdir','/tmp/hueqr-operations-lock'])
 report.finishedAt=new Date().toISOString();await writeFile(join(output,'result.json'),JSON.stringify(report,null,2))
}
console.log(JSON.stringify(report))

