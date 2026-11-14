import assert from 'node:assert/strict'
import {readFile,writeFile} from 'node:fs/promises'
import {join,resolve} from 'node:path'
import {randomBytes,randomUUID,createHash} from 'node:crypto'

// Kiểm API/bytes thật; không gửi view-ack vì script không hiển thị văn bản cho khách.
const [runtimeDirectory,outputFile]=process.argv.slice(2)
assert(runtimeDirectory&&outputFile,'Usage: verify-published-content.mjs runtime private-report.json')
assert(!resolve(outputFile).startsWith(process.cwd()+'\\'),'Keep raw evidence outside repository')
const runtime=JSON.parse(await readFile(join(runtimeDirectory,'runtime.json'),'utf8'))
assert.match(runtime.project,/^hueqr-(ops-check|rehearsal)-[a-f0-9]{8}$/)
const origin=new URL(runtime.url)
assert(origin.protocol==='http:'&&['localhost','127.0.0.1'].includes(origin.hostname))
const bundle=JSON.parse(await readFile('docs/05-noi-dung/demo-content-v02.json','utf8'))
const approved=JSON.parse(await readFile('docs/05-noi-dung/duyet-19-cap-v02-20261114.json','utf8'))
const hash=bytes=>createHash('sha256').update(bytes).digest('hex')
const report={startedAt:new Date().toISOString(),project:runtime.project,status:'RUNNING',realApi:true,physicalDevice:false,viewAcknowledgementsSent:0,providerRequests:0,cases:[]}
async function call(path,credential,body,key,binary=false){
 const response=await fetch(new URL('/api/v1/guest'+path,origin),{method:body===undefined?'GET':'POST',redirect:'error',signal:AbortSignal.timeout(30000),
  headers:{'X-Guest-Credential':credential,Accept:binary?'audio/mpeg':'application/json',...(body==null?{}:{'Content-Type':'application/json'}),...(key?{'Idempotency-Key':key}:{})},body:body==null?undefined:JSON.stringify(body)})
 assert(response.ok,'Guest API status '+response.status)
 if(binary){assert.equal(response.status,200);assert.equal(response.headers.get('content-type').split(';')[0],'audio/mpeg');return Buffer.from(await response.arrayBuffer())}
 return response.json()
}
try{
 for(const language of ['vi','en','fr','ru']){
  const credential=randomBytes(32).toString('base64url');let sessionId
  try{
   const ticket=await call('/start-ticket',credential,null)
   const started=await call('/sessions',credential,{startTicket:ticket.startTicket,displayName:'Kiểm bản phát hành '+language,initialLanguage:language},ticket.requestId)
   sessionId=started.sessionId||started.session?.sessionId;assert(sessionId)
   const documentNonce=randomUUID()
   await call(`/sessions/${sessionId}/resume`,credential,{documentNonce,previousDocumentNonce:null})
   const map=await call(`/sessions/${sessionId}/map`,credential)
   const places=map.places;assert.equal(places.length,5)
   let sequence=0
   for(const article of bundle.articles.filter(a=>a.language===language)){
    const place=places.find(p=>(p.name||p.displayName)===article.placeName);assert(place)
    const payload={placeId:place.id||place.placeId,language,documentNonce,selectionSeq:++sequence},key=randomUUID()
    const content=await call(`/sessions/${sessionId}/openings`,credential,payload,key)
    const repeated=await call(`/sessions/${sessionId}/openings`,credential,payload,key)
    assert.equal(repeated.openingId,content.openingId)
    assert.equal(content.language,language);assert.equal(content.audio.availability,'READY')
    const body=typeof content.text==='string'?content.text:content.text.body
    assert.equal(hash(body),article.textSha256)
    const bytes=await call(`/sessions/${sessionId}/content-grants/${content.grant.id}/audio`,credential,undefined,undefined,true)
    assert.equal(hash(bytes),content.audio.checksum);assert(bytes.length>1000)
    const expected=approved.articles.find(a=>a.placeKey===article.placeKey&&a.language===language)
    if(expected)assert.equal(content.audio.checksum,expected.mp3Checksum)
    report.cases.push({article:article.placeKey+'/'+language,status:'PASSED',textSha256:hash(body),mp3Checksum:hash(bytes),bytes:bytes.length,idempotentReplay:true})
   }
  }finally{if(sessionId)await call(`/sessions/${sessionId}/end`,credential,null,randomUUID())}
 }
 assert.equal(report.cases.length,20);report.status='PASSED'
}catch(error){report.status='FAILED';report.errorName=error.name;await writeFile(outputFile+'.failure-private.txt',String(error.stack));process.exitCode=1}
finally{report.finishedAt=new Date().toISOString();await writeFile(outputFile,JSON.stringify(report,null,2))}
console.log(JSON.stringify({status:report.status,passed:report.cases.length,physicalDevice:false}))
