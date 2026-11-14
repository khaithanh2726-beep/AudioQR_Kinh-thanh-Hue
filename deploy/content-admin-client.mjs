import assert from 'node:assert/strict'
import {randomUUID} from 'node:crypto'

// Client vận hành chỉ vào origin loopback đã chỉ định; không in cookie, password hoặc CSRF.
export async function contentAdmin(runtime) {
 assert.match(runtime.project,/^hueqr-(ops-check|rehearsal)-[a-f0-9]{8}$/)
 const origin=new URL(runtime.url)
 assert(origin.protocol==='http:'&&['localhost','127.0.0.1'].includes(origin.hostname))
 assert(runtime.adminLogin&&runtime.adminPassword)
 const cookies=new Map();let csrf
 async function request(path,method='GET',payload,etag,key){
  assert(/^\/(admin|auth)\//.test(path)&&!path.includes('..'))
  const response=await fetch(new URL('/api/v1'+path,origin),{method,redirect:'error',signal:AbortSignal.timeout(30000),
   headers:{Accept:'application/json',Cookie:[...cookies].map(([k,v])=>k+'='+v).join('; '),
    ...(csrf&&method!=='GET'?{[csrf.headerName]:csrf.token}:{}),...(payload===undefined?{}:{'Content-Type':'application/json'}),
    ...(etag?{'If-Match':etag}:{}),...(key?{'Idempotency-Key':key}:{})},body:payload===undefined?undefined:JSON.stringify(payload)})
  for(const line of response.headers.getSetCookie()){const pair=line.split(';',1)[0],i=pair.indexOf('=');cookies.set(pair.slice(0,i),pair.slice(i+1))}
  const data=response.status===204?null:await response.json()
  return {status:response.status,etag:response.headers.get('etag'),data}
 }
 const first=await request('/auth/csrf');assert.equal(first.status,200);csrf=first.data
 const login=await request('/auth/login','POST',{loginName:runtime.adminLogin,password:runtime.adminPassword});assert.equal(login.status,200)
 csrf=(await request('/auth/csrf')).data
 const read=async path=>{const result=await request(path);assert.equal(result.status,200,'Admin read failed');return result}
 return {request,read,close:()=>request('/auth/logout','POST')}
}

export async function contentCommands(api,ledger,persist){
 const issued=await api.request('/admin/command-windows','POST');assert.equal(issued.status,201)
 return async(name,path,method,payload,etag)=>{
  const previous=ledger.operations.find(step=>step.name===name)
  if(previous){assert.equal(previous.status,'COMMITTED','Inspect the existing receipt before another write');return previous.response.result}
  const step={name,path,method,payload,etag,key:issued.data.windowId+':'+randomUUID(),status:'PENDING'}
  ledger.operations.push(step);await persist()
  const response=await api.request(path,method,payload,etag,step.key)
  step.httpStatus=response.status;step.response=response.data;step.status=response.status>=200&&response.status<300?'COMMITTED':'REQUIRES_INSPECTION'
  await persist();assert.equal(step.status,'COMMITTED',response.data?.code)
  return response.data.result
 }
}
