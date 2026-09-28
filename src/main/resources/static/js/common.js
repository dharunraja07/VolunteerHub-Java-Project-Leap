
const API={events:"/api/events",volunteers:"/api/volunteers",signups:"/api/signups",attendance:"/api/attendance"};
document.addEventListener("DOMContentLoaded",()=>{const p=document.body.dataset.page;document.querySelectorAll("[data-nav]").forEach(a=>a.classList.toggle("active",a.dataset.nav===p));});
async function api(url,opt={}){const r=await fetch(url,{...opt,headers:{"Content-Type":"application/json",...(opt.headers||{})}});const t=await r.text();let d=null;try{d=t?JSON.parse(t):null}catch{d=t}if(!r.ok)throw Error(d?.message||d?.error||"Request failed");return d}
function esc(v){return String(v??"").replace(/[&<>"']/g,c=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"}[c]))}
function initials(v){return String(v||"?").split(" ").filter(Boolean).slice(0,2).map(x=>x[0].toUpperCase()).join("")}
function date(v){return v?new Date(v+"T00:00:00").toLocaleDateString("en-IN",{day:"2-digit",month:"short",year:"numeric"}):"—"}
function alertBox(msg,type="success"){const e=document.getElementById("alert");if(!e)return;e.innerHTML=`<div class="alert ${type}">${esc(msg)}</div>`;setTimeout(()=>e.innerHTML="",4000)}


function closeMobileMenu(){
  document.body.classList.remove("menu-open");
  const btn=document.getElementById("menuToggle");
  if(btn){btn.setAttribute("aria-expanded","false");btn.setAttribute("aria-label","Open navigation");}
}
function openMobileMenu(){
  document.body.classList.add("menu-open");
  const btn=document.getElementById("menuToggle");
  if(btn){btn.setAttribute("aria-expanded","true");btn.setAttribute("aria-label","Close navigation");}
}
document.addEventListener("DOMContentLoaded",()=>{
  const btn=document.getElementById("menuToggle");
  const backdrop=document.getElementById("mobileBackdrop");
  if(btn) btn.addEventListener("click",()=>document.body.classList.contains("menu-open")?closeMobileMenu():openMobileMenu());
  if(backdrop) backdrop.addEventListener("click",closeMobileMenu);
  document.querySelectorAll(".nav a").forEach(a=>a.addEventListener("click",closeMobileMenu));
  document.addEventListener("keydown",e=>{if(e.key==="Escape")closeMobileMenu()});
});
