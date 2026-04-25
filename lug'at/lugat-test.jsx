
// lugat-test.jsx — Daily Test & Settings screens

function TestScreen({ onNavigate }) {
  const qs = TEST_QUESTIONS;
  const [idx, setIdx]       = React.useState(0);
  const [selected, setSelected] = React.useState(null);
  const [answers, setAnswers]   = React.useState([]);
  const [done, setDone]         = React.useState(false);
  const [shake, setShake]       = React.useState(false);

  const q = qs[idx];

  function pick(opt) {
    if (selected !== null) return;
    setSelected(opt);
    const correct = opt === q.correct;
    if (!correct) { setShake(true); setTimeout(()=>setShake(false), 500); }
    const newAnswers = [...answers, { word:q.word, chosen:opt, correct }];
    setAnswers(newAnswers);
    setTimeout(() => {
      const next = idx + 1;
      if (next >= qs.length) { setDone(true); }
      else { setIdx(next); setSelected(null); }
    }, 900);
  }

  const score = answers.filter(a=>a.correct).length;

  if (done) {
    const pct = Math.round((score/qs.length)*100);
    const excellent = pct >= 80;
    return (
      <div style={{ background:C.bg, minHeight:'100%', padding:24,
        fontFamily:'Plus Jakarta Sans, sans-serif', display:'flex',
        flexDirection:'column', gap:14 }}>

        <div style={{ textAlign:'center', paddingTop:16 }}>
          <div style={{ fontSize:64, marginBottom:8 }}>{excellent ? '🏆' : '📖'}</div>
          <div style={{ fontSize:24, fontWeight:800, color:C.onSurface }}>
            {excellent ? "Ajoyib!" : "Yaxshi mashq!"}
          </div>
          <div style={{ fontSize:15, color:C.onSurfaceVariant, marginTop:4 }}>
            {score}/{qs.length} to'g'ri javob
          </div>
        </div>

        {/* Score ring */}
        <div style={{ display:'flex', justifyContent:'center', margin:'8px 0' }}>
          <div style={{ position:'relative' }}>
            <CircleProgress value={score} total={qs.length} size={110} stroke={9}
              color={excellent ? C.correct : C.gold} />
            <div style={{ position:'absolute', inset:0, display:'flex', flexDirection:'column',
              alignItems:'center', justifyContent:'center' }}>
              <div style={{ fontSize:28, fontWeight:800,
                color: excellent ? C.correct : C.gold }}>{pct}%</div>
            </div>
          </div>
        </div>

        {/* Answer review */}
        <div style={{ fontSize:14, fontWeight:700, color:C.onSurface }}>Javoblar</div>
        <div style={{ display:'flex', flexDirection:'column', gap:8 }}>
          {answers.map((a,i)=>(
            <LCard key={i} style={{ padding:'10px 14px',
              background: a.correct ? C.correctBg : C.wrongBg }}>
              <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between' }}>
                <div>
                  <span style={{ fontSize:16, fontWeight:700, color:C.onSurface }}>{a.word}</span>
                  <span style={{ fontSize:12, color:C.onSurfaceVariant, marginLeft:6 }}>→ {a.chosen}</span>
                </div>
                <span style={{ fontSize:18 }}>{a.correct ? '✅' : '❌'}</span>
              </div>
            </LCard>
          ))}
        </div>

        <LBtn onClick={()=>{ setIdx(0); setSelected(null); setAnswers([]); setDone(false); }}
          color={C.primary} onColor="#fff" style={{ padding:'14px', marginTop:4 }}>
          Qayta urinish
        </LBtn>
        <LBtn onClick={()=>onNavigate('home')} variant="outline" style={{ padding:'14px' }}>
          Bosh sahifaga
        </LBtn>
      </div>
    );
  }

  return (
    <div style={{ background:C.bg, minHeight:'100%', paddingBottom:80,
      fontFamily:'Plus Jakarta Sans, sans-serif' }}>

      {/* Header */}
      <div style={{ padding:'20px 20px 16px', display:'flex', alignItems:'center', gap:12 }}>
        <div onClick={()=>onNavigate('home')} style={{ cursor:'pointer', padding:4 }}>
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <path d="M19 12H5M12 5l-7 7 7 7" stroke={C.onSurface} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        </div>
        <div style={{ flex:1 }}>
          <div style={{ fontSize:15, fontWeight:700, color:C.onSurface }}>Kundalik test</div>
        </div>
        <div style={{ fontSize:14, fontWeight:600, color:C.onSurfaceVariant }}>
          {idx+1}/{qs.length}
        </div>
      </div>

      {/* Progress */}
      <div style={{ padding:'0 20px 24px' }}>
        <ProgressBar value={idx} total={qs.length} height={5} />
      </div>

      <div style={{ padding:'0 20px', display:'flex', flexDirection:'column', gap:14 }}>

        {/* Question card */}
        <LCard style={{
          textAlign:'center', padding:32,
          animation: shake ? 'lugat-shake .4s ease' : 'none',
          background: selected !== null
            ? (selected === q.correct ? C.correctBg : C.wrongBg)
            : '#fff',
          transition:'background .25s',
        }}>
          <div style={{ fontSize:12, color:C.outline, fontWeight:600,
            textTransform:'uppercase', letterSpacing:1.2, marginBottom:12 }}>
            Tarjimasini toping
          </div>
          <div style={{ fontSize:48, fontWeight:800, color:C.onSurface, marginBottom:6 }}>
            {q.word}
          </div>
          <div style={{ fontSize:13, color:C.onSurfaceVariant }}>Rus tili</div>
          {selected !== null && (
            <div style={{ marginTop:14, fontSize:15, fontWeight:700,
              color: selected===q.correct ? C.correct : C.error }}>
              {selected===q.correct ? '✅ To\'g\'ri!' : `❌ To'g'ri: ${q.correct}`}
            </div>
          )}
        </LCard>

        {/* Options */}
        <div style={{ display:'flex', flexDirection:'column', gap:10 }}>
          {q.options.map((opt, i) => {
            let bg = '#fff', color = C.onSurface, border = C.outlineVariant;
            if (selected !== null) {
              if (opt === q.correct) { bg = C.correctBg; color = C.correct; border = C.correct; }
              else if (opt === selected) { bg = C.wrongBg; color = C.error; border = C.error; }
            }
            return (
              <div key={i} onClick={()=>pick(opt)} style={{
                padding:'16px 20px', borderRadius:16,
                background: bg, border:`2px solid ${border}`,
                cursor: selected ? 'default' : 'pointer',
                display:'flex', alignItems:'center', gap:12,
                transition:'all .2s',
              }}>
                <div style={{ width:28, height:28, borderRadius:'50%',
                  background: selected===null ? C.surface1 : (opt===q.correct ? C.correctBg : C.wrongBg),
                  border: `2px solid ${border}`,
                  display:'flex', alignItems:'center', justifyContent:'center',
                  fontSize:13, fontWeight:700, color, flexShrink:0 }}>
                  {String.fromCharCode(65+i)}
                </div>
                <span style={{ fontSize:16, fontWeight:600, color }}>{opt}</span>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

// ── Settings Screen ─────────────────────────────────────────
function SettingsScreen() {
  const [dailyGoal, setDailyGoal]   = React.useState(20);
  const [sourceLang, setSourceLang] = React.useState('ru');
  const [notifs, setNotifs]         = React.useState(true);
  const [darkMode, setDarkMode]     = React.useState(false);

  const goals = [5,10,15,20,30,50];

  function Toggle({ value, onChange }) {
    return (
      <div onClick={()=>onChange(!value)} style={{
        width:48, height:28, borderRadius:99, cursor:'pointer',
        background: value ? C.primary : C.outlineVariant,
        position:'relative', transition:'background .2s',
      }}>
        <div style={{
          position:'absolute', top:3, left: value ? 22 : 3,
          width:22, height:22, borderRadius:'50%', background:'#fff',
          boxShadow:'0 1px 3px rgba(0,0,0,0.2)',
          transition:'left .2s',
        }} />
      </div>
    );
  }

  function Row({ label, sub, right }) {
    return (
      <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between',
        padding:'14px 0', borderBottom:`1px solid ${C.outlineVariant}` }}>
        <div>
          <div style={{ fontSize:15, fontWeight:600, color:C.onSurface }}>{label}</div>
          {sub && <div style={{ fontSize:12, color:C.onSurfaceVariant, marginTop:2 }}>{sub}</div>}
        </div>
        {right}
      </div>
    );
  }

  return (
    <div style={{ background:C.bg, minHeight:'100%', paddingBottom:80,
      fontFamily:'Plus Jakarta Sans, sans-serif' }}>

      <div style={{ padding:'20px 20px 12px' }}>
        <div style={{ fontSize:22, fontWeight:800, color:C.onSurface }}>Sozlamalar</div>
      </div>

      {/* Profile card */}
      <div style={{ padding:'0 16px 14px' }}>
        <LCard style={{ display:'flex', alignItems:'center', gap:14, padding:'16px' }}>
          <div style={{ width:56, height:56, borderRadius:'50%', background:C.primaryContainer,
            display:'flex', alignItems:'center', justifyContent:'center',
            fontSize:24, fontWeight:700, color:C.onPrimaryContainer, flexShrink:0 }}>A</div>
          <div style={{ flex:1 }}>
            <div style={{ fontSize:16, fontWeight:700, color:C.onSurface }}>Alisher</div>
            <div style={{ fontSize:13, color:C.onSurfaceVariant }}>alisher@example.com</div>
          </div>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <path d="M9 18l6-6-6-6" stroke={C.outline} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        </LCard>
      </div>

      <div style={{ padding:'0 16px', display:'flex', flexDirection:'column', gap:14 }}>

        {/* Learning settings */}
        <LCard>
          <div style={{ fontSize:12, fontWeight:700, color:C.onSurfaceVariant,
            textTransform:'uppercase', letterSpacing:1, marginBottom:8 }}>O'rganish</div>

          {/* Daily goal */}
          <div style={{ paddingBottom:14, borderBottom:`1px solid ${C.outlineVariant}` }}>
            <div style={{ display:'flex', justifyContent:'space-between', marginBottom:10 }}>
              <div>
                <div style={{ fontSize:15, fontWeight:600, color:C.onSurface }}>Kunlik maqsad</div>
                <div style={{ fontSize:12, color:C.onSurfaceVariant }}>Har kuni o'rganiladigan so'zlar</div>
              </div>
              <div style={{ fontSize:20, fontWeight:800, color:C.primary }}>{dailyGoal}</div>
            </div>
            <div style={{ display:'flex', gap:6 }}>
              {goals.map(g=>(
                <div key={g} onClick={()=>setDailyGoal(g)} style={{
                  flex:1, padding:'8px 0', borderRadius:10, textAlign:'center',
                  background: dailyGoal===g ? C.primary : C.surface1,
                  color: dailyGoal===g ? '#fff' : C.onSurfaceVariant,
                  fontSize:13, fontWeight:700, cursor:'pointer',
                  transition:'background .15s',
                }}>{g}</div>
              ))}
            </div>
          </div>

          {/* Source language */}
          <div style={{ paddingTop:12 }}>
            <div style={{ fontSize:15, fontWeight:600, color:C.onSurface, marginBottom:8 }}>
              O'rganish tili
            </div>
            <div style={{ display:'flex', gap:8 }}>
              {[
                { code:'ru', flag:'🇷🇺', label:'Ruscha' },
                { code:'en', flag:'🇬🇧', label:'Inglizcha' },
                { code:'both', flag:'🌐', label:'Ikkala' },
              ].map(l=>(
                <div key={l.code} onClick={()=>setSourceLang(l.code)} style={{
                  flex:1, padding:'10px 6px', borderRadius:12, textAlign:'center', cursor:'pointer',
                  background: sourceLang===l.code ? C.primaryContainer : C.surface1,
                  border: `2px solid ${sourceLang===l.code ? C.primary : 'transparent'}`,
                  transition:'all .15s',
                }}>
                  <div style={{ fontSize:20 }}>{l.flag}</div>
                  <div style={{ fontSize:11, fontWeight:600, color:C.onSurface, marginTop:4 }}>{l.label}</div>
                </div>
              ))}
            </div>
          </div>
        </LCard>

        {/* Notifications */}
        <LCard>
          <div style={{ fontSize:12, fontWeight:700, color:C.onSurfaceVariant,
            textTransform:'uppercase', letterSpacing:1, marginBottom:4 }}>Bildirishnomalar</div>
          <Row label="Kundalik eslatma" sub="Har kuni soat 09:00" right={<Toggle value={notifs} onChange={setNotifs}/>} />
          <Row label="Seriya ogohlantirishlari" sub="Serriyangiz uzilishidan oldin" right={<Toggle value={true} onChange={()=>{}}/>} />
          <div style={{ paddingTop:14, display:'flex', gap:8 }}>
            {['07:00','09:00','12:00','19:00','21:00'].map(t=>(
              <div key={t} onClick={()=>{}} style={{
                flex:1, padding:'8px 4px', borderRadius:10, textAlign:'center',
                background: t==='09:00' ? C.primaryContainer : C.surface1,
                fontSize:11, fontWeight:700, cursor:'pointer',
                color: t==='09:00' ? C.onPrimaryContainer : C.onSurfaceVariant,
              }}>{t}</div>
            ))}
          </div>
        </LCard>

        {/* About */}
        <LCard>
          <div style={{ fontSize:12, fontWeight:700, color:C.onSurfaceVariant,
            textTransform:'uppercase', letterSpacing:1, marginBottom:4 }}>Ilova haqida</div>
          <Row label="Versiya" right={<span style={{ fontSize:13, color:C.onSurfaceVariant }}>1.0.0</span>} />
          <Row label="Lug'at bazasi" right={<span style={{ fontSize:13, color:C.onSurfaceVariant }}>6000 so'z</span>} />
          <Row label="Offline rejim" right={<span style={{ fontSize:13, color:C.correct, fontWeight:600 }}>✓ Faol</span>} />
        </LCard>

      </div>
    </div>
  );
}

Object.assign(window, { TestScreen, SettingsScreen });
