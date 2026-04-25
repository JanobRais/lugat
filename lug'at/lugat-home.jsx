
// lugat-home.jsx — Home & Stats screens

function HomeScreen({ onNavigate }) {
  const todayGoal = 20;
  const todayDone = 7;

  return (
    <div style={{ background:C.bg, minHeight:'100%', paddingBottom:80,
      fontFamily:'Plus Jakarta Sans, sans-serif' }}>

      {/* ── Header ── */}
      <div style={{ padding:'20px 20px 8px', display:'flex', alignItems:'center', justifyContent:'space-between' }}>
        <div>
          <div style={{ fontSize:13, color:C.onSurfaceVariant, fontWeight:500 }}>Salom,</div>
          <div style={{ fontSize:22, fontWeight:700, color:C.onSurface }}>Alisher 👋</div>
        </div>
        <div style={{ display:'flex', alignItems:'center', gap:8 }}>
          {/* Streak badge */}
          <div style={{ display:'flex', alignItems:'center', gap:4, background:'#fff3ee',
            borderRadius:99, padding:'6px 12px' }}>
            <span style={{ fontSize:18 }}>🔥</span>
            <span style={{ fontSize:15, fontWeight:700, color:C.streak }}>5</span>
          </div>
          {/* Avatar */}
          <div style={{ width:40, height:40, borderRadius:'50%', background:C.primaryContainer,
            display:'flex', alignItems:'center', justifyContent:'center',
            fontSize:18, fontWeight:700, color:C.onPrimaryContainer }}>A</div>
        </div>
      </div>

      <div style={{ padding:'8px 16px', display:'flex', flexDirection:'column', gap:12 }}>

        {/* ── Daily Progress Card ── */}
        <LCard style={{ background: `linear-gradient(135deg, ${C.primary} 0%, #00897b 100%)`, color:'#fff' }}>
          <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between' }}>
            <div style={{ flex:1 }}>
              <div style={{ fontSize:13, opacity:0.8, fontWeight:500, marginBottom:4 }}>Kunlik maqsad</div>
              <div style={{ fontSize:28, fontWeight:800, marginBottom:2 }}>{todayDone}
                <span style={{ fontSize:16, fontWeight:500, opacity:0.7 }}>/{todayGoal}</span>
              </div>
              <div style={{ fontSize:13, opacity:0.75, marginBottom:12 }}>so'z o'rganildi</div>
              <div style={{ background:'rgba(255,255,255,0.25)', borderRadius:99, height:8, overflow:'hidden' }}>
                <div style={{ width:`${(todayDone/todayGoal)*100}%`, height:'100%',
                  background:'#fff', borderRadius:99, transition:'width .4s' }} />
              </div>
            </div>
            <div style={{ position:'relative', marginLeft:16 }}>
              <CircleProgress value={todayDone} total={todayGoal} size={80} stroke={7} color="rgba(255,255,255,0.9)" />
              <div style={{ position:'absolute', inset:0, display:'flex', flexDirection:'column',
                alignItems:'center', justifyContent:'center' }}>
                <div style={{ fontSize:18, fontWeight:800 }}>{Math.round((todayDone/todayGoal)*100)}%</div>
              </div>
            </div>
          </div>
          <div style={{ marginTop:14, display:'flex', gap:8 }}>
            <LBtn onClick={()=>onNavigate('flashcard')}
              color="rgba(255,255,255,0.18)" onColor="#fff" variant="fill"
              style={{ flex:1, fontSize:13, padding:'10px 12px' }}>
              🃏 Kartalar
            </LBtn>
            <LBtn onClick={()=>onNavigate('test')}
              color="rgba(255,255,255,0.18)" onColor="#fff" variant="fill"
              style={{ flex:1, fontSize:13, padding:'10px 12px' }}>
              ✏️ Test
            </LBtn>
          </div>
        </LCard>

        {/* ── Streak Week ── */}
        <LCard>
          <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between', marginBottom:12 }}>
            <div style={{ fontSize:15, fontWeight:700, color:C.onSurface }}>🔥 5 kunlik seriya!</div>
            <span style={{ fontSize:12, color:C.onSurfaceVariant, fontWeight:500 }}>Bu hafta</span>
          </div>
          <div style={{ display:'flex', justifyContent:'space-between' }}>
            {WEEKLY.map((d,i) => (
              <div key={i} style={{ display:'flex', flexDirection:'column', alignItems:'center', gap:6 }}>
                <div style={{
                  width:36, height:36, borderRadius:'50%',
                  background: d.done ? C.streak : C.surface1,
                  display:'flex', alignItems:'center', justifyContent:'center',
                  fontSize: d.done ? 18 : 13, color: d.done ? '#fff' : C.outlineVariant,
                  fontWeight:700,
                }}>
                  {d.done ? '🔥' : '·'}
                </div>
                <span style={{ fontSize:11, color: d.done ? C.onSurface : C.outlineVariant, fontWeight:600 }}>{d.day}</span>
              </div>
            ))}
          </div>
        </LCard>

        {/* ── Continue Learning ── */}
        <div style={{ fontSize:15, fontWeight:700, color:C.onSurface, paddingLeft:4, marginBottom:-4 }}>
          Davom eting
        </div>

        <LCard onClick={()=>onNavigate('learn')} style={{ display:'flex', alignItems:'center', gap:14 }}>
          <div style={{ width:52, height:52, borderRadius:16, background:C.primaryContainer,
            display:'flex', alignItems:'center', justifyContent:'center', fontSize:26, flexShrink:0 }}>
            🇷🇺
          </div>
          <div style={{ flex:1, minWidth:0 }}>
            <div style={{ fontSize:14, fontWeight:700, color:C.onSurface }}>Trilingual 2000</div>
            <div style={{ fontSize:12, color:C.onSurfaceVariant, marginBottom:8 }}>Unit 2 · 340/2000 so'z</div>
            <ProgressBar value={340} total={2000} />
          </div>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <path d="M9 18l6-6-6-6" stroke={C.outline} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        </LCard>

        <LCard onClick={()=>onNavigate('learn')} style={{ display:'flex', alignItems:'center', gap:14 }}>
          <div style={{ width:52, height:52, borderRadius:16, background:'#fff3e0',
            display:'flex', alignItems:'center', justifyContent:'center', fontSize:26, flexShrink:0 }}>
            🇬🇧
          </div>
          <div style={{ flex:1, minWidth:0 }}>
            <div style={{ fontSize:14, fontWeight:700, color:C.onSurface }}>Essential 4000</div>
            <div style={{ fontSize:12, color:C.onSurfaceVariant, marginBottom:8 }}>Unit 3 · 823/4000 so'z</div>
            <ProgressBar value={823} total={4000} color={C.gold} />
          </div>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <path d="M9 18l6-6-6-6" stroke={C.outline} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        </LCard>

        {/* ── Word of the Day ── */}
        <div style={{ fontSize:15, fontWeight:700, color:C.onSurface, paddingLeft:4, marginBottom:-4 }}>
          Kunning so'zi
        </div>
        <LCard style={{ background: C.surface1 }}>
          <div style={{ display:'flex', justifyContent:'space-between', alignItems:'flex-start' }}>
            <div>
              <div style={{ fontSize:28, fontWeight:800, color:C.primary, marginBottom:4 }}>синий</div>
              <div style={{ fontSize:15, color:C.onSurface, fontWeight:600, marginBottom:2 }}>ko'k · blue</div>
              <div style={{ fontSize:12, color:C.onSurfaceVariant }}>sifat · adjective</div>
            </div>
            <div style={{ padding:'4px 10px', background:C.primaryContainer, borderRadius:99,
              fontSize:12, fontWeight:600, color:C.onPrimaryContainer }}>Yangi</div>
          </div>
        </LCard>

      </div>
    </div>
  );
}

// ── Stats Screen ────────────────────────────────────────────
function StatsScreen() {
  const totalLearned = 1163;
  const totalWords   = 6000;
  const triLearned   = 340;
  const essLearned   = 823;
  const daily = [5,12,18,8,20,14,7];

  return (
    <div style={{ background:C.bg, minHeight:'100%', paddingBottom:80,
      fontFamily:'Plus Jakarta Sans, sans-serif' }}>

      <div style={{ padding:'20px 20px 8px' }}>
        <div style={{ fontSize:22, fontWeight:800, color:C.onSurface }}>Statistika</div>
        <div style={{ fontSize:13, color:C.onSurfaceVariant, marginTop:2 }}>So'nggi 7 kun</div>
      </div>

      <div style={{ padding:'4px 16px', display:'flex', flexDirection:'column', gap:12 }}>

        {/* ── Big numbers ── */}
        <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:10 }}>
          {[
            { label:"Jami o'rganildi", value:totalLearned, icon:'📚', color:C.primary, bg:C.primaryContainer },
            { label:'Kunlik seriya', value:'5 kun', icon:'🔥', color:C.streak, bg:'#fff3ee' },
            { label:'Bugun', value:'7 so\'z', icon:'⚡', color:'#6750a4', bg:'#ede9fd' },
            { label:'Aniqlik', value:'82%', icon:'🎯', color:C.correct, bg:C.correctBg },
          ].map((s,i)=>(
            <LCard key={i} style={{ padding:14 }}>
              <div style={{ fontSize:22, marginBottom:4 }}>{s.icon}</div>
              <div style={{ fontSize:20, fontWeight:800, color:s.color }}>{s.value}</div>
              <div style={{ fontSize:11, color:C.onSurfaceVariant, fontWeight:500, marginTop:2 }}>{s.label}</div>
            </LCard>
          ))}
        </div>

        {/* ── Weekly bar chart ── */}
        <LCard>
          <div style={{ fontSize:14, fontWeight:700, color:C.onSurface, marginBottom:14 }}>
            Haftalik faollik
          </div>
          <div style={{ display:'flex', alignItems:'flex-end', gap:6, height:80 }}>
            {daily.map((v,i)=>(
              <div key={i} style={{ flex:1, display:'flex', flexDirection:'column', alignItems:'center', gap:4 }}>
                <div style={{
                  width:'100%', borderRadius:'6px 6px 0 0',
                  height: `${(v/20)*80}px`,
                  background: i===6 ? C.outlineVariant : C.primary,
                  opacity: i===6 ? 1 : 0.85,
                  transition:'height .4s ease',
                }} />
                <span style={{ fontSize:10, color:C.onSurfaceVariant, fontWeight:600 }}>
                  {WEEKLY[i].day}
                </span>
              </div>
            ))}
          </div>
        </LCard>

        {/* ── Vocabulary progress ── */}
        <LCard>
          <div style={{ fontSize:14, fontWeight:700, color:C.onSurface, marginBottom:14 }}>
            Lug'at holati
          </div>
          {[
            { label:'Trilingual 2000', value:triLearned, total:2000, color:C.primary },
            { label:'Essential 4000', value:essLearned, total:4000, color:C.gold },
          ].map((l,i)=>(
            <div key={i} style={{ marginBottom: i===0?14:0 }}>
              <div style={{ display:'flex', justifyContent:'space-between', marginBottom:6 }}>
                <span style={{ fontSize:13, fontWeight:600, color:C.onSurface }}>{l.label}</span>
                <span style={{ fontSize:12, color:C.onSurfaceVariant }}>{l.value}/{l.total}</span>
              </div>
              <ProgressBar value={l.value} total={l.total} color={l.color} height={8} />
            </div>
          ))}
        </LCard>

        {/* ── Achievements ── */}
        <div style={{ fontSize:15, fontWeight:700, color:C.onSurface, paddingLeft:4, marginBottom:-4 }}>
          Yutuqlar
        </div>
        <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr 1fr', gap:10 }}>
          {[
            { icon:'🔥', label:'5 kun', desc:'Seriya', active:true  },
            { icon:'📖', label:'100+', desc:"O'rganildi", active:true  },
            { icon:'🏆', label:'Unit 1', desc:'Tugatildi', active:true  },
            { icon:'⚡', label:'Tez', desc:"O'quvchi", active:false },
            { icon:'🌟', label:'500+', desc:"So'z", active:false },
            { icon:'🎯', label:'90%', desc:'Aniqlik', active:false },
          ].map((a,i)=>(
            <LCard key={i} style={{ padding:12, textAlign:'center', opacity: a.active ? 1 : 0.45 }}>
              <div style={{ fontSize:24, marginBottom:4 }}>{a.icon}</div>
              <div style={{ fontSize:13, fontWeight:700, color:C.onSurface }}>{a.label}</div>
              <div style={{ fontSize:10, color:C.onSurfaceVariant }}>{a.desc}</div>
            </LCard>
          ))}
        </div>

      </div>
    </div>
  );
}

Object.assign(window, { HomeScreen, StatsScreen });
