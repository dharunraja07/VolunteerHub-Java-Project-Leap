
let data=[];
const $=id=>document.getElementById(id);
async function load(){try{data=await api(API.events);render(data)}catch(e){$("rows").innerHTML=`<tr><td colspan="5" class="empty">${esc(e.message)}</td></tr>`}}
function render(a){$("rows").innerHTML=a.length?a.map(x=>`<tr><td><div class="cell"><div class="avatar">E</div><div><strong>${esc(x.title)}</strong><div class="muted">${esc(x.description)}</div></div></div></td><td>${date(x.eventDate)}</td><td>${esc(x.location)}</td><td>${x.capacity}</td><td><div class="table-actions"><button class="btn ghost small" onclick="edit(${x.id})">Edit</button><button class="btn danger small" onclick="del(${x.id})">Delete</button></div></td></tr>`).join(""):'<tr><td colspan="5" class="empty">No events found.</td></tr>'}
function openEvent(x=null){$("modal").classList.remove("hidden");$("modalTitle").textContent=x?"Edit event":"Create event";$("id").value=x?.id||"";$("title").value=x?.title||"";$("description").value=x?.description||"";$("date").value=x?.eventDate||"";$("location").value=x?.location||"";$("capacity").value=x?.capacity||""}
function closeEvent(){$("modal").classList.add("hidden")} function edit(i){openEvent(data.find(x=>x.id===i))}
async function del(i){if(!confirm("Delete this event?"))return;try{await api(`${API.events}/${i}`,{method:"DELETE"});alertBox("Event deleted successfully.");load()}catch(e){alertBox(e.message,"error")}}
$("form").addEventListener("submit",async e=>{e.preventDefault();const i=$("id").value;const body={title:$("title").value.trim(),description:$("description").value.trim(),eventDate:$("date").value,location:$("location").value.trim(),capacity:Number($("capacity").value)};try{await api(i?`${API.events}/${i}`:API.events,{method:i?"PUT":"POST",body:JSON.stringify(body)});closeEvent();alertBox(i?"Event updated successfully.":"Event created successfully.");load()}catch(e){alertBox(e.message,"error")}})
$("search").addEventListener("input",e=>{const q=e.target.value.toLowerCase();render(data.filter(x=>`${x.title} ${x.description} ${x.location}`.toLowerCase().includes(q)))});
load();
