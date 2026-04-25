
// lugat-learn.jsx — Learn, Word List, and Flashcard screens

function LearnScreen({ onNavigate }) {
  const [tab, setTab] = React.useState('trilingual');
  const [search, setSearch] = React.useState('');

  const filtered = TRILINGUAL.filter(w =>
    !search ||
    w.ru.includes(search.toLowerCase()) ||
    w.uz.toLowerCase().includes(search.toLowerCase()) ||
    w.en.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div style={{ background:C.bg, minHeight:'100%', paddingBottom:80,
      fontFamily:'Plus Jakarta Sans, sans-serif' }}>

      {/* Header */}
      <div style={{ padding:'20px 16px 12px', background:C.bg }}>
        <div style={{ fontSize:22, fontWeight:800, color:C.onSurface, marginBottom:14 }}>Lug'atlar</div>
        {/* Search */}
        <div style={{ display:'flex', alignItems:'center', background:'#fff',
          borderRadius:99, padding:'10px 16px', gap:10,
          boxShadow:'0 1px 4px rgba(0,0,0,0.07)' }}>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <circle cx="11" cy="11" r="7" stroke={C.outline} strokeWidth="2"/>
            <path d="M16.5 16.5L21 21" stroke={C.outline} strokeWidth="2" strokeLinecap="round"/>
          </svg>
          <input value={search} onChange={e=>setSearch(e.target.value)}
            placeholder="So'z qidirish..."
            style={{ border:'none', outline:'none', flex:1, fontSize:14,
              fontFamily:'Plus Jakarta Sans, sans-serif', color:C.onSurface,
              background:'transparent' }} />
          {search && <div onClick={()=>setSearch('')}
            style={{ color:C.outline, cursor:'pointer', fontSize:16, lineHeight:1 }}>×</div>}
        </div>
      </div>

      {/* Tabs */}
      <div style={{ display:'flex', gap:8, padding:'0 16px 14px', overflowX:'auto' }}>
        <Chip label="🇷🇺 Trilingual 2000" active={tab==='trilingual'} onClick={()=>setTab('trilingual')} />
        <Chip label="🇬🇧 Essential 4000"  active={tab==='essential'}  onClick={()=>setTab('essential')}  />
      </div>

      {tab === 'trilingual' ? (
        <div style={{ padding:'0 16px', display:'flex', flexDirection:'column', gap:8 }}>

          {/* Progress summary */}
          <LCard style={{ background: C.primaryContainer, padding:'14px 18px' }}>
            <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between', marginBottom:10 }}>
              <div>
                <div style={{ fontSize:13, fontWeight:600, color:C.onPrimaryContainer }}>Umumiy progress</div>
                <div style={{ fontSize:24, fontWeight:800, color:C.primary }}>340
                  <span style={{ fontSize:14, fontWeight:500 }}>/2000</span></div>
              </div>
              <div style={{ position:'relative' }}>
                <CircleProgress value={340} total={2000} size={68} stroke={6} color={C.primary} />
                <div style={{ position:'absolute', inset:0, display:'flex', alignItems:'center',
                  justifyContent:'center', fontSize:13, fontWeight:700, color:C.primary }}>17%</div>
              </div>
            </div>
            <LBtn onClick={()=>onNavigate('flashcard')} color={C.primary} onColor="#fff" variant="fill"
              style={{ width:'100%', padding:'11px' }}>
              O'rganishni davom ettirish →
            </LBtn>
          </LCard>

          {/* Word list */}
          {filtered.map(w => (
            <LCard key={w.id} style={{ padding:'12px 16px' }}>
              <div style={{ display:'flex', alignItems:'center', gap:12 }}>
                <div style={{ width:8, height:8, borderRadius:'50%', flexShrink:0,
                  background: w.learned ? C.correct : C.outlineVariant }} />
                <div style={{ flex:1 }}>
                  <div style={{ display:'flex', alignItems:'baseline', gap:8 }}>
                    <span style={{ fontSize:16, fontWeight:700, color:C.onSurface }}>{w.ru}</span>
                    <span style={{ fontSize:12, color:C.outline }}>рус</span>
                  </div>
                  <div style={{ fontSize:13, color:C.onSurfaceVariant, marginTop:2 }}>
                    <span style={{ fontWeight:600, color:C.primary }}>{w.uz}</span>
                    <span style={{ margin:'0 6px', color:C.outlineVariant }}>·</span>
                    <span>{w.en}</span>
                  </div>
                </div>
                {w.learned && (
                  <div style={{ background:C.correctBg, borderRadius:99, padding:'3px 10px',
                    fontSize:11, fontWeight:600, color:C.correct }}>✓</div>
                )}
              </div>
            </LCard>
          ))}
        </div>
      ) : (
        <div style={{ padding:'0 16px', display:'flex', flexDirection:'column', gap:8 }}>

          {/* Progress summary */}
          <LCard style={{ background:'#fff8e1', padding:'14px 18px' }}>
            <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between', marginBottom:10 }}>
              <div>
                <div style={{ fontSize:13, fontWeight:600, color:'#6d4c00' }}>Umumiy progress</div>
                <div style={{ fontSize:24, fontWeight:800, color:C.gold }}>823
                  <span style={{ fontSize:14, fontWeight:500, color:'#6d4c00' }}>/4000</span></div>
              </div>
              <div style={{ position:'relative' }}>
                <CircleProgress value={823} total={4000} size={68} stroke={6} color={C.gold} />
                <div style={{ position:'absolute', inset:0, display:'flex', alignItems:'center',
                  justifyContent:'center', fontSize:13, fontWeight:700, color:C.gold }}>21%</div>
              </div>
            </div>
          </LCard>

          {/* Units */}
          {ESSENTIAL_UNITS.map(u => (
            <LCard key={u.id} style={{ padding:'14px 16px' }}>
              <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between', marginBottom:8 }}>
                <div style={{ fontSize:14, fontWeight:600, color:C.onSurface }}>{u.title}</div>
                <span style={{ fontSize:12, color:C.onSurfaceVariant }}>{u.learned}/{u.total}</span>
              </div>
              <ProgressBar value={u.learned} total={u.total} color={C.gold} height={5} />
              {u.learned === u.total && (
                <div style={{ marginTop:8, fontSize:12, fontWeight:600, color:C.correct }}>✓ Tugatildi</div>
              )}
            </LCard>
          ))}
        </div>
      )}
    </div>
  );
}

// ── Flashcard Screen ────────────────────────────────────────
function FlashcardScreen({ onNavigate }) {
  const words = TRILINGUAL.slice(0, 8);
  const [idx, setIdx]       = React.useState(0);
  const [flipped, setFlipped] = React.useState(false);
  const [known, setKnown]   = React.useState([]);
  const [done, setDone]     = React.useState(false);
  const [animDir, setAnimDir] = React.useState(null); // 'left'|'right'

  const current = words[idx];

  function go(dir) {
    setAnimDir(dir);
    setFlipped(false);
    setTimeout(() => {
      if (dir === 'right') setKnown(k => [...k, current.id]);
      const next = idx + 1;
      if (next >= words.length) { setDone(true); }
      else { setIdx(next); }
      setAnimDir(null);
    }, 220);
  }

  if (done) return (
    <div style={{ background:C.bg, minHeight:'100%', display:'flex',
      flexDirection:'column', alignItems:'center', justifyContent:'center',
      padding:24, fontFamily:'Plus Jakarta Sans, sans-serif', gap:16 }}>
      <div style={{ fontSize:64 }}>🎉</div>
      <div style={{ fontSize:24, fontWeight:800, color:C.onSurface, textAlign:'center' }}>
        Mashq tugadi!
      </div>
      <div style={{ fontSize:15, color:C.onSurfaceVariant, textAlign:'center' }}>
        <span style={{ color:C.primary, fontWeight:700 }}>{known.length}</span> so'z bilasiz,{' '}
        <span style={{ color:C.streak, fontWeight:700 }}>{words.length - known.length}</span> qayta o'rganasiz
      </div>
      <LCard style={{ width:'100%', padding:20, textAlign:'center' }}>
        <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:8 }}>
          <div>
            <div style={{ fontSize:32, fontWeight:800, color:C.correct }}>{known.length}</div>
            <div style={{ fontSize:12, color:C.onSurfaceVariant }}>Bilaman ✓</div>
          </div>
          <div>
            <div style={{ fontSize:32, fontWeight:800, color:C.streak }}>{words.length - known.length}</div>
            <div style={{ fontSize:12, color:C.onSurfaceVariant }}>Qayta</div>
          </div>
        </div>
      </LCard>
      <LBtn onClick={()=>{ setIdx(0); setFlipped(false); setKnown([]); setDone(false); }}
        color={C.primary} onColor="#fff" style={{ width:'100%', padding:'14px' }}>
        Qayta boshlash
      </LBtn>
      <LBtn onClick={()=>onNavigate('home')} variant="outline"
        style={{ width:'100%', padding:'14px' }}>
        Bosh sahifaga
      </LBtn>
    </div>
  );

  return (
    <div style={{ background:C.bg, minHeight:'100%', paddingBottom:80,
      fontFamily:'Plus Jakarta Sans, sans-serif' }}>

      {/* Header */}
      <div style={{ padding:'20px 20px 16px', display:'flex', alignItems:'center', gap:12 }}>
        <div onClick={()=>onNavigate('learn')} style={{ cursor:'pointer', padding:4 }}>
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <path d="M19 12H5M12 5l-7 7 7 7" stroke={C.onSurface} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        </div>
        <div style={{ flex:1 }}>
          <div style={{ fontSize:15, fontWeight:700, color:C.onSurface }}>Flesh-kartalar</div>
          <div style={{ fontSize:12, color:C.onSurfaceVariant }}>Trilingual 2000</div>
        </div>
        <div style={{ fontSize:14, fontWeight:600, color:C.onSurfaceVariant }}>
          {idx+1}/{words.length}
        </div>
      </div>

      {/* Progress */}
      <div style={{ padding:'0 20px', marginBottom:24 }}>
        <ProgressBar value={idx} total={words.length} height={4} />
      </div>

      {/* Card */}
      <div style={{ padding:'0 20px' }}>
        <div onClick={()=>setFlipped(f=>!f)} style={{
          cursor:'pointer', userSelect:'none',
          transform: animDir==='left' ? 'translateX(-30px)' : animDir==='right' ? 'translateX(30px)' : 'none',
          opacity: animDir ? 0 : 1,
          transition: animDir ? 'transform .2s ease, opacity .2s ease' : 'none',
        }}>
          <LCard style={{ minHeight:220, display:'flex', flexDirection:'column',
            alignItems:'center', justifyContent:'center', gap:10,
            background: flipped ? C.primaryContainer : '#fff', padding:28 }}>
            {!flipped ? (
              <>
                <div style={{ fontSize:12, color:C.outline, fontWeight:600, textTransform:'uppercase',
                  letterSpacing:1.2 }}>Rus tili</div>
                <div style={{ fontSize:44, fontWeight:800, color:C.onSurface, textAlign:'center' }}>
                  {current.ru}
                </div>
                <div style={{ fontSize:13, color:C.onSurfaceVariant, marginTop:8 }}>
                  Ko'rish uchun bosing
                </div>
              </>
            ) : (
              <>
                <div style={{ fontSize:12, color:C.primary, fontWeight:600, textTransform:'uppercase',
                  letterSpacing:1.2 }}>Tarjimalar</div>
                <div style={{ fontSize:13, color:C.outline, fontWeight:600 }}>Rus: {current.ru}</div>
                <div style={{ display:'flex', flexDirection:'column', alignItems:'center', gap:6, marginTop:4 }}>
                  <div style={{ display:'flex', alignItems:'center', gap:8 }}>
                    <span style={{ fontSize:18 }}>🇺🇿</span>
                    <span style={{ fontSize:30, fontWeight:800, color:C.primary }}>{current.uz}</span>
                  </div>
                  <div style={{ display:'flex', alignItems:'center', gap:8 }}>
                    <span style={{ fontSize:18 }}>🇬🇧</span>
                    <span style={{ fontSize:30, fontWeight:800, color:C.onSurface }}>{current.en}</span>
                  </div>
                </div>
              </>
            )}
          </LCard>
        </div>

        {/* Hint */}
        {!flipped && (
          <div style={{ textAlign:'center', marginTop:12, fontSize:12, color:C.outlineVariant }}>
            👆 Ko'rish uchun bosing
          </div>
        )}

        {/* Action buttons */}
        <div style={{ display:'flex', gap:12, marginTop:24 }}>
          <LBtn onClick={()=>go('left')} variant="fill"
            color={C.wrongBg} onColor={C.error}
            style={{ flex:1, padding:'14px', fontSize:15 }}>
            😕 Bilmadim
          </LBtn>
          <LBtn onClick={()=>go('right')} variant="fill"
            color={C.correctBg} onColor={C.correct}
            style={{ flex:1, padding:'14px', fontSize:15 }}>
            ✅ Bilaman
          </LBtn>
        </div>

        {/* Known count */}
        <div style={{ textAlign:'center', marginTop:14, fontSize:12, color:C.onSurfaceVariant }}>
          {known.length} ta so'z bilasiz
        </div>
      </div>
    </div>
  );
}

Object.assign(window, { LearnScreen, FlashcardScreen });
