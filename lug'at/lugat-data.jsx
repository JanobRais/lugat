
// lugat-data.jsx — shared data, constants, and micro-components

const C = {
  primary: '#006a60',
  onPrimary: '#ffffff',
  primaryContainer: '#9cf2e0',
  onPrimaryContainer: '#00201c',
  secondary: '#4a6360',
  secondaryContainer: '#cce8e3',
  onSecondaryContainer: '#041f1d',
  surface: '#f4fbf8',
  surface1: '#e8f5f1',
  surface2: '#ddf0eb',
  onSurface: '#161d1b',
  onSurfaceVariant: '#3f4946',
  outline: '#6f7976',
  outlineVariant: '#bec9c5',
  bg: '#f4fbf8',
  streak: '#f4511e',
  gold: '#f9aa33',
  error: '#ba1a1a',
  errorContainer: '#ffdad6',
  correct: '#1b7f5a',
  correctBg: '#d4f7ea',
  wrongBg: '#ffdad6',
};

const TRILINGUAL = [
  { id:1,  ru:'дом',       uz:'uy',          en:'house',     learned:true  },
  { id:2,  ru:'вода',      uz:'suv',         en:'water',     learned:true  },
  { id:3,  ru:'хлеб',      uz:'non',         en:'bread',     learned:true  },
  { id:4,  ru:'время',     uz:'vaqt',        en:'time',      learned:true  },
  { id:5,  ru:'человек',   uz:'odam',        en:'person',    learned:true  },
  { id:6,  ru:'год',       uz:'yil',         en:'year',      learned:true  },
  { id:7,  ru:'рука',      uz:"qo'l",        en:'hand',      learned:true  },
  { id:8,  ru:'глаз',      uz:"ko'z",        en:'eye',       learned:false },
  { id:9,  ru:'работа',    uz:'ish',         en:'work',      learned:false },
  { id:10, ru:'день',      uz:'kun',         en:'day',       learned:false },
  { id:11, ru:'город',     uz:'shahar',      en:'city',      learned:false },
  { id:12, ru:'книга',     uz:'kitob',       en:'book',      learned:false },
  { id:13, ru:'школа',     uz:'maktab',      en:'school',    learned:false },
  { id:14, ru:'машина',    uz:'mashina',     en:'car',       learned:false },
  { id:15, ru:'еда',       uz:'taom',        en:'food',      learned:false },
];

const ESSENTIAL_UNITS = [
  { id:1, title:'Unit 1: Everyday Essentials',    total:20, learned:20 },
  { id:2, title:'Unit 2: Home & Family',           total:20, learned:15 },
  { id:3, title:'Unit 3: Work & Career',           total:20, learned:8  },
  { id:4, title:'Unit 4: Travel & Transport',      total:20, learned:3  },
  { id:5, title:'Unit 5: Food & Dining',           total:20, learned:0  },
  { id:6, title:'Unit 6: Health & Body',           total:20, learned:0  },
  { id:7, title:'Unit 7: Nature & Environment',    total:20, learned:0  },
  { id:8, title:'Unit 8: Technology',              total:20, learned:0  },
];

const TEST_QUESTIONS = [
  { word:'дом',     correct:'uy',          options:["uy","suv","non","vaqt"]    },
  { word:'вода',    correct:'suv',         options:["odam","suv","yil","ish"]   },
  { word:'хлеб',    correct:'non',         options:["non","kun","ko'z","qo'l"]  },
  { word:'время',   correct:'vaqt',        options:["kitob","shahar","vaqt","mashina"] },
  { word:'человек', correct:'odam',        options:["taom","odam","maktab","kun"]      },
  { word:'год',     correct:'yil',         options:["yil","uy","ish","non"]     },
  { word:'рука',    correct:"qo'l",        options:["suv","kun","qo'l","vaqt"]  },
  { word:'глаз',    correct:"ko'z",        options:["ko'z","shahar","kitob","ish"] },
];

const WEEKLY = [
  { day:'Du', done:true  },
  { day:'Se', done:true  },
  { day:'Ch', done:true  },
  { day:'Pa', done:true  },
  { day:'Ju', done:true  },
  { day:'Sh', done:false },
  { day:'Ya', done:false },
];

// ── Shared micro-components ──────────────────────────────────

function LCard({ children, style={}, onClick }) {
  return (
    <div onClick={onClick} style={{
      background:'#fff', borderRadius:20, padding:16,
      boxShadow:'0 1px 4px rgba(0,0,0,0.08)',
      cursor: onClick ? 'pointer' : 'default',
      transition:'transform .15s,box-shadow .15s',
      ...style
    }}
    onMouseDown={e=>{ if(onClick) e.currentTarget.style.transform='scale(0.98)'; }}
    onMouseUp={e=>{ if(onClick) e.currentTarget.style.transform=''; }}
    onMouseLeave={e=>{ if(onClick) e.currentTarget.style.transform=''; }}
    >
      {children}
    </div>
  );
}

function LBtn({ children, variant='fill', color=C.primary, onColor=C.onPrimary, onClick, style={} }) {
  const fill = variant==='fill';
  return (
    <div onClick={onClick} style={{
      display:'flex', alignItems:'center', justifyContent:'center', gap:6,
      padding:'12px 20px', borderRadius:100,
      background: fill ? color : 'transparent',
      border: fill ? 'none' : `1.5px solid ${C.outlineVariant}`,
      color: fill ? onColor : color,
      fontFamily:'Plus Jakarta Sans, sans-serif',
      fontWeight:600, fontSize:14, cursor:'pointer',
      userSelect:'none', transition:'opacity .15s',
      ...style
    }}
    onMouseDown={e=>e.currentTarget.style.opacity='0.75'}
    onMouseUp={e=>e.currentTarget.style.opacity='1'}
    onMouseLeave={e=>e.currentTarget.style.opacity='1'}
    >
      {children}
    </div>
  );
}

function ProgressBar({ value, total, color=C.primary, height=6 }) {
  const pct = Math.round((value/total)*100);
  return (
    <div style={{ background:C.outlineVariant, borderRadius:99, height, overflow:'hidden' }}>
      <div style={{
        width:`${pct}%`, height:'100%',
        background:color, borderRadius:99,
        transition:'width .4s ease',
      }} />
    </div>
  );
}

function CircleProgress({ value, total, size=72, stroke=6, color=C.primary }) {
  const r = (size-stroke*2)/2;
  const circ = 2*Math.PI*r;
  const pct = value/total;
  return (
    <svg width={size} height={size} style={{ transform:'rotate(-90deg)' }}>
      <circle cx={size/2} cy={size/2} r={r} fill="none" stroke={C.outlineVariant} strokeWidth={stroke}/>
      <circle cx={size/2} cy={size/2} r={r} fill="none" stroke={color} strokeWidth={stroke}
        strokeDasharray={circ} strokeDashoffset={circ*(1-pct)}
        strokeLinecap="round" style={{ transition:'stroke-dashoffset .5s ease' }}/>
    </svg>
  );
}

function Chip({ label, active, onClick }) {
  return (
    <div onClick={onClick} style={{
      padding:'6px 14px', borderRadius:99, cursor:'pointer',
      background: active ? C.primaryContainer : C.surface1,
      color: active ? C.onPrimaryContainer : C.onSurfaceVariant,
      fontFamily:'Plus Jakarta Sans, sans-serif', fontSize:13, fontWeight:600,
      userSelect:'none', transition:'background .2s',
      whiteSpace:'nowrap',
    }}>
      {label}
    </div>
  );
}

Object.assign(window, { C, TRILINGUAL, ESSENTIAL_UNITS, TEST_QUESTIONS, WEEKLY, LCard, LBtn, ProgressBar, CircleProgress, Chip });
