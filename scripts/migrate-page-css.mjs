import fs from 'fs';
import path from 'path';

const srcPagesDir = path.join('src', 'main', 'resources', 'static', 'css', 'pages');
const targetPagesDir = path.join('target', 'classes', 'static', 'css', 'pages');

const globalReplacements = [
  ['var(--surface-muted)', 'var(--surface-secondary)'],
  ['var(--border-strong)', 'var(--divider)'],
  ['var(--primary-dark)', 'var(--primary-hover)'],
  ['var(--primary-soft)', 'var(--brand-blue-light)'],
  ['var(--success-soft)', 'var(--brand-green-light)'],
  ['var(--danger-soft)', 'var(--danger-light)'],
  ['var(--transition)', 'var(--transition-fast)'],

  ['var(--blue-dark)', 'var(--brand-blue-hover)'],
  ['var(--blue-light)', 'var(--brand-blue-lighter)'],
  ['var(--blue-tint)', 'var(--brand-blue-lighter)'],
  ['var(--blue-mid)', 'var(--brand-blue-light)'],
  ['var(--blue)', 'var(--brand-blue)'],
  ['var(--green-dark)', 'var(--accent-green-hover)'],
  ['var(--green-light)', 'var(--accent-green-light)'],
  ['var(--green)', 'var(--accent-green)'],
  ['var(--card)', 'var(--background)'],
  ['var(--text-muted)', 'var(--text-secondary)'],
  ['var(--font-display)', 'var(--font-heading)'],
  ['var(--font-body)', 'var(--font-primary)'],
  ['var(--font-h)', 'var(--font-heading)'],
  ['var(--font-b)', 'var(--font-primary)'],
  ['var(--font-m)', 'var(--font-mono)'],
  ['var(--font-head)', 'var(--font-heading)'],
  ['var(--coral)', 'var(--danger)'],
  ['var(--amber)', 'var(--warning)'],
  ['var(--white)', 'var(--surface)'],
  ['var(--ink)', 'var(--text-primary)'],
  ['var(--ink-soft)', 'var(--text-secondary)'],
  ['var(--ink-faint)', 'var(--text-muted)'],
  ['var(--line)', 'var(--border-light)'],
  ['var(--card-border)', 'var(--border-light)'],
  ['var(--ease-bounce)', 'var(--ease-bounce)'],
  ['var(--ease)', 'var(--ease-smooth)'],

  ['var(--pf-blue-dark)', 'var(--brand-blue-hover)'],
  ['var(--pf-blue-light)', 'var(--brand-blue-lighter)'],
  ['var(--pf-blue-mid)', 'var(--brand-blue-light)'],
  ['var(--pf-blue)', 'var(--brand-blue)'],
  ['var(--pf-green-dark)', 'var(--accent-green-hover)'],
  ['var(--pf-green-light)', 'var(--accent-green-light)'],
  ['var(--pf-green)', 'var(--accent-green)'],
  ['var(--pf-amber)', 'var(--warning)'],
  ['var(--pf-red-light)', 'var(--danger-light)'],
  ['var(--pf-red-dark)', '#B91C1C'],
  ['var(--pf-red)', 'var(--danger)'],
  ['var(--pf-text-soft)', 'var(--text-secondary)'],
  ['var(--pf-text)', 'var(--text-primary)'],
  ['var(--pf-muted)', 'var(--text-muted)'],
  ['var(--pf-border)', 'var(--border)'],
  ['var(--pf-surface)', 'var(--surface-hover)'],
  ['var(--pf-bg)', 'var(--surface)'],
  ['var(--pf-font-h)', 'var(--font-heading)'],
  ['var(--pf-font-b)', 'var(--font-primary)'],
  ['var(--pf-ease-bounce)', 'var(--ease-bounce)'],
  ['var(--pf-ease)', 'var(--ease-smooth)'],
  ['var(--pf-r-xs)', 'var(--radius-sm)'],
  ['var(--pf-r-sm)', 'var(--radius-md)'],
  ['var(--pf-r-md)', 'var(--radius-lg)'],
  ['var(--pf-r-lg)', 'var(--radius-xl)'],
  ['var(--pf-r-xl)', 'var(--radius-2xl)'],
  ['var(--pf-r-2xl)', 'var(--radius-2xl)'],
  ['var(--pf-sh-sm)', 'var(--shadow-sm)'],
  ['var(--pf-sh-md)', 'var(--shadow-md)'],
  ['var(--pf-sh-lg)', 'var(--shadow-lg)'],
  ['var(--pf-sh-xs)', 'var(--shadow-sm)'],
  ['var(--pf-sh-blue)', '0 8px 24px rgba(37,99,235,.20)'],
  ['var(--pf-sh-glow)', '0 0 0 3px rgba(37,99,235,.20)'],

  ['var(--ep-blue-dark)', 'var(--brand-blue-hover)'],
  ['var(--ep-blue-light)', 'var(--brand-blue-lighter)'],
  ['var(--ep-blue-mid)', 'var(--brand-blue-light)'],
  ['var(--ep-blue)', 'var(--brand-blue)'],
  ['var(--ep-green-dark)', 'var(--accent-green-hover)'],
  ['var(--ep-green-light)', 'var(--accent-green-light)'],
  ['var(--ep-green)', 'var(--accent-green)'],
  ['var(--ep-red-light)', 'var(--danger-light)'],
  ['var(--ep-red)', 'var(--danger)'],
  ['var(--ep-amber)', 'var(--warning)'],
  ['var(--ep-text-soft)', 'var(--text-secondary)'],
  ['var(--ep-text)', 'var(--text-primary)'],
  ['var(--ep-muted)', 'var(--text-muted)'],
  ['var(--ep-border)', 'var(--border)'],
  ['var(--ep-surface)', 'var(--surface-hover)'],
  ['var(--ep-bg)', 'var(--surface)'],
  ['var(--ep-font-h)', 'var(--font-heading)'],
  ['var(--ep-font-b)', 'var(--font-primary)'],
  ['var(--ep-ease)', 'var(--ease-smooth)'],
  ['var(--ep-r-sm)', 'var(--radius-md)'],
  ['var(--ep-r-md)', 'var(--radius-lg)'],
  ['var(--ep-r-lg)', 'var(--radius-xl)'],
  ['var(--ep-sh-sm)', 'var(--shadow-sm)'],
  ['var(--ep-sh-md)', 'var(--shadow-md)'],
  ['var(--ep-sh-lg)', 'var(--shadow-lg)'],
  ['var(--ep-glow)', '0 0 0 4px rgba(37,99,235,.12)'],

  ['var(--s-blue-dark)', 'var(--brand-blue-hover)'],
  ['var(--s-blue-tint)', 'var(--brand-blue-lighter)'],
  ['var(--s-blue-mid)', 'var(--brand-blue-light)'],
  ['var(--s-blue)', 'var(--brand-blue)'],
  ['var(--s-green-dark)', 'var(--accent-green-hover)'],
  ['var(--s-green-tint)', 'var(--accent-green-light)'],
  ['var(--s-green)', 'var(--accent-green)'],
  ['var(--s-ink-soft)', 'var(--text-secondary)'],
  ['var(--s-ink-faint)', 'var(--text-muted)'],
  ['var(--s-ink)', 'var(--text-primary)'],
  ['var(--s-text-light)', 'var(--text-muted)'],
  ['var(--s-text)', 'var(--text-primary)'],
  ['var(--s-bg)', 'var(--background)'],
  ['var(--s-white)', 'var(--surface)'],
  ['var(--s-border)', 'var(--border-light)'],
  ['var(--s-shadow-sm)', 'var(--shadow-sm)'],
  ['var(--s-shadow-md)', 'var(--shadow-md)'],
  ['var(--s-shadow-lg)', 'var(--shadow-lg)'],
  ['var(--s-radius-sm)', 'var(--radius-md)'],
  ['var(--s-radius-md)', 'var(--radius-lg)'],
  ['var(--s-radius-lg)', 'var(--radius-xl)'],
  ['var(--s-radius-xl)', 'var(--radius-2xl)'],
  ['var(--s-ease)', 'var(--ease-smooth)'],
  ['var(--s-head)', 'var(--font-heading)'],
  ['var(--s-body)', 'var(--font-primary)'],

  ['var(--hrm-blue-dark)', 'var(--brand-blue-hover)'],
  ['var(--hrm-blue-tint)', 'var(--brand-blue-lighter)'],
  ['var(--hrm-blue)', 'var(--brand-blue)'],
  ['var(--hrm-green)', 'var(--accent-green)'],
  ['var(--hrm-bg)', 'var(--background)'],
  ['var(--hrm-card)', 'var(--surface)'],
  ['var(--hrm-border)', 'var(--border)'],
  ['var(--hrm-text-soft)', 'var(--text-secondary)'],
  ['var(--hrm-text)', 'var(--text-primary)'],
  ['var(--hrm-danger)', 'var(--danger)'],
  ['var(--hrm-radius-card)', 'var(--radius-lg)'],
  ['var(--hrm-radius-ctrl)', 'var(--radius-md)'],
  ['var(--hrm-shadow)', 'var(--shadow-md)'],
  ['var(--hrm-shadow-modal)', 'var(--shadow-xl)'],
  ['var(--hrm-ease)', 'var(--ease-smooth)'],
  ['var(--hrm-head)', 'var(--font-heading)'],
  ['var(--hrm-body)', 'var(--font-primary)'],

  ['var(--error)', 'var(--danger)'],
  ['var(--r-sm)', 'var(--radius-md)'],
  ['var(--r-md)', 'var(--radius-lg)'],
  ['var(--r-lg)', 'var(--radius-2xl)'],
  ['var(--sh-sm)', 'var(--shadow-sm)'],
  ['var(--sh-md)', 'var(--shadow-md)'],
  ['var(--sh-lg)', 'var(--shadow-lg)'],

  ['var(--text-soft)', 'var(--text-secondary)'],
  ['var(--red-light)', 'var(--danger-light)'],
  ['var(--red)', 'var(--danger)'],
  ['var(--purple-light)', 'var(--purple-light)'],
  ['var(--r-xs)', 'var(--radius-sm)'],
  ['var(--sh-xs)', 'var(--shadow-sm)'],
  ['var(--sh-blue)', '0 8px 24px rgba(37,99,235,.20)'],
  ['var(--sh-glow)', '0 0 0 3px rgba(37,99,235,.20)'],

  ['var(--blue-line)', 'var(--brand-blue-light)'],
  ['var(--faint)', 'var(--text-disabled)'],

  // Radius placeholders (avoid cascade)
  ['var(--radius-sm)', '__RADIUS_SM__'],
  ['var(--radius-md)', '__RADIUS_MD__'],
  ['var(--radius-lg)', '__RADIUS_LG__'],
  ['var(--radius-xl)', '__RADIUS_XL__'],

  // Generic names after prefixed tokens
  ['var(--muted)', 'var(--text-muted)'],
  ['var(--text)', 'var(--text-primary)'],
];

const radiusFinal = [
  ['__RADIUS_SM__', 'var(--radius-md)'],
  ['__RADIUS_MD__', 'var(--radius-lg)'],
  ['__RADIUS_LG__', 'var(--radius-2xl)'],
  ['__RADIUS_XL__', 'var(--radius-2xl)'],
];

const fileSpecific = {
  'Projectedit.css': [
    ['var(--bg)', 'var(--background)'],
  ],
  'Skilledit.css': [
    ['var(--bg)', 'var(--background)'],
  ],
  'Home.css': [
    ['var(--bg)', 'var(--home-bg)'],
  ],
  'Homepage1.css': [
    ['var(--bg)', 'var(--surface)'],
  ],
  'HomePage2.css': [
    ['var(--bg)', 'var(--surface)'],
  ],
  'SignUp.css': [
    ['var(--bg)', 'var(--surface)'],
  ],
  'Expert.css': [
    ['var(--bg)', 'var(--surface)'],
  ],
};

const rootBlockPattern = /^:root\s*\{[\s\S]*?\}\s*\n/m;
const indentedRootBlockPattern = /^[ \t]*:root\s*\{[\s\S]*?\}\s*\n/m;

const resetPatterns = [
  /^\* \{ box-sizing: border-box; margin: 0; padding: 0; \}\s*\n/m,
  /^\* \{ box-sizing: border-box; \}\s*\n/m,
  /^\*, \*::before, \*::after \{ box-sizing: border-box; margin: 0; padding: 0; \}\s*\n/m,
  /^\*, \*::before, \*::after \{ box-sizing: border-box; \}\s*\n/m,
  /^\*, \*::before, \*::after \{\s*\n[\s\S]*?box-sizing: border-box;[\s\S]*?\}\s*\n/m,
  /^\*,\*::before,\*::after\{ box-sizing:border-box; margin:0; padding:0; \}\s*\n/m,
  /^[ \t]*\*, \*::before, \*::after \{ box-sizing: border-box; margin: 0; padding: 0; \}\s*\n/m,
  /^[ \t]*\*, \*::before, \*::after \{ box-sizing: border-box; \}\s*\n/m,
  /^[ \t]*\*,\*::before,\*::after\{ box-sizing:border-box; margin:0; padding:0; \}\s*\n/m,
  /^html \{ scroll-behavior: smooth; \}\s*\n/m,
  /^html \{ scroll-behavior: smooth; -webkit-text-size-adjust: 100%; \}\s*\n/m,
  /^html\{ -webkit-text-size-adjust:100%; \}\s*\n/m,
  /^html  \{ scroll-behavior: smooth; \}\s*\n/m,
  /^[ \t]*html \{ scroll-behavior: smooth; -webkit-text-size-adjust: 100%; \}\s*\n/m,
  /^[ \t]*html\{ -webkit-text-size-adjust:100%; \}\s*\n/m,
  /^html, body \{ margin: 0; min-height: 100%; font-family: Inter, system-ui, sans-serif; background: var\(--bg\); color: var\(--text\); \}\s*\n/m,
  /^html, body \{\s*\n[\s\S]*?\}\s*\n/m,
  /^body \{ padding: 0; \}\s*\n/m,
  /^body \{ min-height: 100vh; \}\s*\n/m,
  /^body  \{ font-family:[\s\S]*?\}\s*\n/m,
  /^body   \{ font-family:[\s\S]*?\}\s*\n/m,
  /^body \{\s*\n[\s\S]*?font-family:[\s\S]*?\}\s*\n/m,
  /^[ \t]*body \{\s*\n[\s\S]*?font-family: var\(--font-primary\);[\s\S]*?\}\s*\n/m,
  /^[ \t]*body\{\s*\n[\s\S]*?font-family: var\(--font-primary\);[\s\S]*?\}\s*\n/m,
  /^a \{ text-decoration: none; color: inherit; \}\s*\n/m,
  /^a      \{ text-decoration: none; color: inherit; \}\s*\n/m,
  /^[ \t]*a \{ text-decoration: none; color: inherit; \}\s*\n/m,
  /^[ \t]*a\{ text-decoration:none; color:inherit; \}\s*\n/m,
  /^ul \{ list-style: none; \}\s*\n/m,
  /^ul     \{ list-style: none; \}\s*\n/m,
  /^[ \t]*ul, ol \{ list-style: none; \}\s*\n/m,
  /^[ \t]*ul\{ list-style:none; \}\s*\n/m,
  /^svg \{ display: block; \}\s*\n/m,
  /^[ \t]*img \{ display: block; max-width: 100%; \}\s*\n/m,
  /^[ \t]*svg \{ display: block;[\s\S]*?\}\s*\n/m,
  /^button, input, select \{ font: inherit; \}\s*\n/m,
  /^button \{ cursor: pointer; \}\s*\n/m,
  /^[ \t]*button \{ font-family: var\(--font-primary\); cursor: pointer; \}\s*\n/m,
  /^[ \t]*button\{ font-family: var\(--font-primary\); cursor:pointer; background:none; border:none; \}\s*\n/m,
  /^\/\* ── RESET \(scoped\) ── \*\/\s*\n[\s\S]*?button \{ font-family: inherit; cursor: pointer; \}\s*\n/m,
  /^\/\* ── Reset ──[\s\S]*?@media \(prefers-reduced-motion: reduce\)[\s\S]*?\}\s*\n/m,
];

const staleCommentPatterns = [
  /\/\* ── DESIGN TOKENS ── \*\/\s*\n/g,
  /\/\* ── RESET \(scoped\) ── \*\/\s*\n/g,
  /\/\* ── Design tokens \(mirrors Profile\.css palette\) ── \*\/\s*\n/g,
  /\/\* =+\s*\n\s*TOKENS\s*\n\s*=+\s*\*\/\s*\n/g,
  /\/\* ── Tokens ── \*\/\s*\n/g,
  /\/\* ── TOKENS ── \*\/\s*\n/g,
];

const genericReducedMotionPatterns = [
  /^@media \(prefers-reduced-motion: reduce\) \{\s*\n\s*\*, \*::before, \*::after \{\s*\n[\s\S]*?\}\s*\n(?:\s*html \{ scroll-behavior: auto; \}\s*\n)?\}\s*\n/m,
  /^[ \t]*@media \(prefers-reduced-motion: reduce\) \{\s*\n[\s\S]*?transition-duration:[\s\S]*?\}\s*\n\}\s*\n/m,
  /^[ \t]*@media \(prefers-reduced-motion: reduce\)\{[^}]*\}\s*\n/m,
];

const headerComment = `/* Layout and page-specific styles only — tokens/reset from app.css */\n\n`;

function applyReplacements(content, replacements) {
  for (const [from, to] of replacements) {
    content = content.split(from).join(to);
  }
  return content;
}

function stripDuplicateResets(content) {
  for (const pattern of staleCommentPatterns) {
    content = content.replace(pattern, '');
  }

  let previous;
  do {
    previous = content;
    for (const pattern of resetPatterns) {
      content = content.replace(pattern, '');
    }
    for (const pattern of genericReducedMotionPatterns) {
      content = content.replace(pattern, '');
    }
  } while (content !== previous);

  return content;
}

function normalizePageHeader(content) {
  content = content.replace(/^\s*\n{3,}/gm, '\n\n');
  content = content.replace(/^\/\* Layout and page-specific styles only — tokens\/reset from app\.css \*\/\s*\n+/m, '');
  return headerComment + content.trimStart();
}

function migrateFile(fileName) {
  const srcPath = path.join(srcPagesDir, fileName);
  const targetPath = path.join(targetPagesDir, fileName);
  const sourcePath = fs.existsSync(targetPath) ? targetPath : srcPath;

  let content = fs.readFileSync(sourcePath, 'utf8');
  if (!content.includes(':root')) {
    if (sourcePath === targetPath) {
      return { file: fileName, changed: false, reason: 'no :root in target' };
    }
    return { file: fileName, changed: false, reason: 'no :root' };
  }

  content = content.replace(rootBlockPattern, '');
  content = content.replace(indentedRootBlockPattern, '');

  content = stripDuplicateResets(content);

  content = content.replace(/\/\* =+\s*\n\s*KNOWLY[^\n]*\n[\s\S]*?=+\s*\*\/\s*\n/g, '');
  content = content.replace(/\/\* ── Design tokens ──[\s\S]*?─+\s*\*\/\s*\n/g, '');

  content = applyReplacements(content, globalReplacements);
  content = applyReplacements(content, fileSpecific[fileName] ?? [['var(--bg)', 'var(--background)']]);
  content = applyReplacements(content, radiusFinal);

  content = normalizePageHeader(content);

  fs.writeFileSync(srcPath, content);
  return { file: fileName, changed: true };
}

function cleanupFile(fileName) {
  const srcPath = path.join(srcPagesDir, fileName);
  const original = fs.readFileSync(srcPath, 'utf8');
  let content = stripDuplicateResets(original);
  content = normalizePageHeader(content);

  if (content === original) {
    return { file: fileName, changed: false };
  }

  fs.writeFileSync(srcPath, content);
  return { file: fileName, changed: true };
}

const cleanupOnly = process.argv.includes('--cleanup');
const skip = new Set(['error.css', 'Login.css']);
const files = fs.readdirSync(srcPagesDir).filter((f) => f.endsWith('.css') && !skip.has(f));
const results = cleanupOnly ? files.map(cleanupFile) : files.map(migrateFile);
console.log(JSON.stringify(results, null, 2));
