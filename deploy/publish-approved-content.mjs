import assert from 'node:assert/strict'
import {readFile,writeFile,mkdir} from 'node:fs/promises'
import {join,resolve} from 'node:path'
import {createHash} from 'node:crypto'
import {execFileSync} from 'node:child_process'
import {pathToFileURL} from 'node:url'
import {contentAdmin,contentCommands} from './content-admin-client.mjs'
import {plainDirectory,disjoint} from './operations-retention.mjs'

const [runtimeArg,credentialSourceArg,outputArg]=process.argv.slice(2)
assert(outputArg,'Usage: publish-approved-content.mjs runtime credential-source new-private-ledger')
const runtimeRoot=await plainDirectory(runtimeArg),output=resolve(outputArg)
disjoint(runtimeRoot,output);disjoint(process.cwd(),output)
const json=async path=>JSON.parse((await readFile(path,'utf8')).replace(/^\uFEFF/,''))
const runtime=await json(join(runtimeRoot,'runtime.json')),credential=await json(join(credentialSourceArg,'runtime.json'))
const recordBytes=await readFile('docs/05-noi-dung/duyet-19-cap-v02-20261114.json'),record=JSON.parse(recordBytes)
assert.equal(record.status,'APPROVED_FOR_PUBLICATION');assert.equal(record.articles.length,19)
const regions=await json('docs/05-noi-dung/duyet-vung-demo-v02.json');assert.equal(regions.status,'APPROVED_FOR_COURSE_DEMO')
const {prepareRegion}=await import(pathToFileURL(resolve('frontend/src/admin/geometry.js')))
await mkdir(output)
const sid=execFileSync('powershell.exe',['-NoProfile','-Command','[Security.Principal.WindowsIdentity]::GetCurrent().User.Value'],{encoding:'utf8',windowsHide:true,stdio:'pipe'}).trim()
assert.match(sid,/^S-1-[0-9-]+$/)
execFileSync('icacls.exe',[output,'/inheritance:r','/grant:r',`*${sid}:(OI)(CI)F`,'*S-1-5-18:(OI)(CI)F'],{windowsHide:true,stdio:'pipe'})
const ledger={startedAt:new Date().toISOString(),status:'RUNNING',project:runtime.project,approvalSha256:createHash('sha256').update(recordBytes).digest('hex'),operations:[],articles:[],regions:[],providerRequests:0}
const persist=()=>writeFile(join(output,'ledger.json'),JSON.stringify(ledger,null,2))
await persist()
const api=await contentAdmin({...runtime,adminLogin:credential.adminLogin,adminPassword:credential.adminPassword})
try{
 const command=await contentCommands(api,ledger,persist)
 const places=(await api.read('/admin/places')).data
 const originalNgo=places.find(p=>p.displayName==='Cổng Ngọ Môn');assert(originalNgo?.visible)
 const ngoBefore=(await api.read(`/admin/places/${originalNgo.placeId}/content/vi`)).data.appliedPair
 assert(ngoBefore)
 for(const region of regions.regions){
  const place=places.find(p=>p.displayName===region.name);assert(place&&!place.visible)
  const path='/admin/places/'+place.placeId
  let detail=await api.read(path)
  const geometry=await prepareRegion({closed:true,vertices:region.polygon.coordinates[0].slice(0,-1)})
  assert.equal(geometry.geometryHash,region.geometryHash)
  const saved=await command('region-save/'+region.placeKey,path+'/region-revisions','POST',geometry,detail.etag)
  const reloaded=(await api.read(path+'/region-revisions/'+saved.regionRevisionId)).data
  const checked=await prepareRegion({closed:true,vertices:reloaded.polygon.coordinates[0].slice(0,-1)})
  assert.equal(checked.geometryHash,region.geometryHash)
  detail=await api.read(path)
  await command('region-apply/'+region.placeKey,path+'/active-region','PUT',{savedRevisionId:saved.regionRevisionId,previewedGeometryHash:checked.geometryHash},detail.etag)
  ledger.regions.push({placeKey:region.placeKey,status:'APPLIED',geometryHash:checked.geometryHash});await persist()
 }
 for(const article of record.articles){
  const place=places.find(p=>p.displayName===article.placeName);assert(place)
  const path=`/admin/places/${place.placeId}/content/${article.language}`
  let detail=(await api.read(path)).data
  assert.equal(detail.draftText.textRevisionId,article.textRevisionId);assert.equal(detail.draftText.contentHash,article.textSha256)
  const locator='docs/05-noi-dung/duyet-19-cap-v02-20261114.json#'+article.article
  const limitations='Chủ đồ án duyệt bộ v02 hiện có cho bản học phần; không phải chứng nhận chuyên gia. Kiểm mẫu máy ở lượt cuối.'+(article.article==='thai-hoa/fr'?' Giữ ghi chú kỹ thuật về thuật ngữ kiến trúc Thái Hòa fr.':'')
  const textPath='/admin/text-revisions/'+article.textRevisionId+'/review-reference'
  const textBefore=await api.read(textPath)
  await command('text-review/'+article.article,textPath,'PUT',{locator,limitations},textBefore.etag)
  const audioPath='/admin/audio-revisions/'+article.audioRevisionId+'/review-reference'
  const audioBefore=await api.read(audioPath);assert.equal(audioBefore.data.mp3Checksum,article.mp3Checksum)
  await command('audio-review/'+article.article,audioPath,'PUT',{locator,limitations},audioBefore.etag)
  const text=(await api.read(textPath)).data,audio=(await api.read(audioPath)).data
  const current=await api.read(path)
  await command('pair-apply/'+article.article,path+'/active-pair','PUT',{textRevisionId:article.textRevisionId,audioRevisionId:article.audioRevisionId,
   expectedTextHash:article.textSha256,expectedMp3Checksum:article.mp3Checksum,textReviewVersion:text.version,audioReviewVersion:audio.version},current.etag)
  detail=(await api.read(path)).data
  assert.equal(detail.appliedPair.audioRevisionId,article.audioRevisionId);assert.deepEqual(detail.appliedPair.missingConditions,[])
  ledger.articles.push({article:article.article,status:'APPLIED',textSha256:article.textSha256,mp3Checksum:article.mp3Checksum});await persist()
  console.log(JSON.stringify({article:article.article,status:'APPLIED'}))
 }
 for(const place of places.filter(p=>p.placeId!==originalNgo.placeId)){
  const path='/admin/places/'+place.placeId,current=await api.read(path)
  await command('show/'+place.placeId,path+'/visibility','PUT',{visible:true},current.etag)
 }
 assert.equal((await api.read('/admin/places')).data.filter(p=>p.visible).length,5)
 assert.equal((await api.read(`/admin/places/${originalNgo.placeId}/content/vi`)).data.appliedPair.pairId,ngoBefore.pairId)
 Object.assign(ledger,{status:'PUBLISHED_FIVE_PLACES_TWENTY_PAIRS',previousNgoMonPairPreserved:true,visiblePlaces:5,appliedPairs:20})
}catch(error){await writeFile(join(output,'failure-private.txt'),String(error.stack));ledger.status='PUBLISH_REQUIRES_INSPECTION';ledger.errorName=error.name;process.exitCode=1}
finally{await api.close();ledger.finishedAt=new Date().toISOString();await persist()}
console.log(JSON.stringify({status:ledger.status,articlesApplied:ledger.articles.length,visiblePlaces:ledger.visiblePlaces,providerRequests:0}))
