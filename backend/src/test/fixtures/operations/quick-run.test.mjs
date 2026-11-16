import test from 'node:test'
import assert from 'node:assert/strict'
import {mayResume,validateContainers} from '../../../../../deploy/quick-run.mjs'

const project='hueqr-rehearsal-1234abcd',owner='61f63d43-7fc5-405d-946a-4969c7e37e81'
test('only resumes its completed stop with matching project and owner',()=>{
  const guard={mode:'FROZEN',owner},receipt={project,status:'STOPPED',owner}
  assert(mayResume(project,guard,receipt))
  for(const other of [null,{...receipt,project:'another'},{...receipt,status:'STOPPING'},{...receipt,owner:'different'}])assert(!mayResume(project,guard,other))
  assert(!mayResume(project,{...guard,mode:'QUIESCING'},receipt))
})
function container(service){return {Name:'/'+project+'-'+service+'-1',Config:{Labels:{'com.docker.compose.project':project,'com.docker.compose.service':service}},Mounts:[{Type:'volume',Name:project+'_'+(service==='application'?'audio-data':'mysql-data'),Destination:service==='application'?'/data/audio':'/var/lib/mysql'}],HostConfig:{PortBindings:service==='application'?{'8080/tcp':[{HostIp:'127.0.0.1'}]}:{}}}}
test('rejects wrong project, storage or public MySQL before lifecycle actions',()=>{
  validateContainers({project},container('application'),container('database'))
  const wrongDb=container('database');wrongDb.HostConfig.PortBindings['3306/tcp']=[{HostIp:'0.0.0.0'}]
  assert.throws(()=>validateContainers({project},container('application'),wrongDb))
  const wrongApp=container('application');wrongApp.Mounts[0].Name='other_audio-data'
  assert.throws(()=>validateContainers({project},wrongApp,container('database')))
  assert.throws(()=>validateContainers({project:'hueqr-ops-check-1234abcd'},container('application'),container('database')))
})

test('main project accepts only explicitly preserved volume identities',()=>{
  const app=container('application'),db=container('database')
  for(const [c,service]of [[app,'application'],[db,'database']]){
    c.Name='/audioqr-hue-'+service+'-1'
    c.Config.Labels['com.docker.compose.project']='audioqr-hue'
  }
  const runtime={project:'audioqr-hue',volumeNames:{application:project+'_audio-data',database:project+'_mysql-data'}}
  validateContainers(runtime,app,db)
  assert.throws(()=>validateContainers({project:'audioqr-hue'},app,db))
})
