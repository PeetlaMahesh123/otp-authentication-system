const channelEl = document.getElementById('channel');
const identifierEl = document.getElementById('identifier');
const identifierLabelEl = document.getElementById('identifierLabel');
const requestBtn = document.getElementById('requestBtn');
const requestMsg = document.getElementById('requestMsg');
const step2 = document.getElementById('step2');
const codeEl = document.getElementById('code');
const verifyBtn = document.getElementById('verifyBtn');
const verifyMsg = document.getElementById('verifyMsg');

channelEl.addEventListener('change', () => {
  if (channelEl.value === 'email') {
    identifierLabelEl.textContent = 'Email address';
    identifierEl.placeholder = 'you@example.com';
  } else {
    identifierLabelEl.textContent = 'Phone number';
    identifierEl.placeholder = '+15551234567';
  }
});

requestBtn.addEventListener('click', async () => {
  requestMsg.textContent = '';
  verifyMsg.textContent = '';
  const channel = channelEl.value;
  const identifier = identifierEl.value.trim();
  if (!identifier) {
    requestMsg.textContent = 'Please enter your email or phone number';
    requestMsg.className = 'msg error';
    return;
  }
  try {
    const res = await fetch('/api/otp/request', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ channel, identifier })
    });
    const data = await res.json();
    if (res.ok && data.status === 'ok') {
      requestMsg.textContent = 'OTP sent. Check your ' + channel + '.';
      requestMsg.className = 'msg ok';
      step2.classList.remove('hidden');
    } else {
      requestMsg.textContent = data.message || 'Failed to send OTP';
      requestMsg.className = 'msg error';
    }
  } catch (e) {
    requestMsg.textContent = 'Network error';
    requestMsg.className = 'msg error';
  }
});

verifyBtn.addEventListener('click', async () => {
  verifyMsg.textContent = '';
  const channel = channelEl.value;
  const identifier = identifierEl.value.trim();
  const code = codeEl.value.trim();
  if (!code) {
    verifyMsg.textContent = 'Enter the OTP code';
    verifyMsg.className = 'msg error';
    return;
  }
  try {
    const res = await fetch('/api/otp/verify', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ channel, identifier, code })
    });
    const data = await res.json();
    if (res.ok && data.status === 'ok') {
      verifyMsg.textContent = 'Authenticated';
      verifyMsg.className = 'msg ok';
    } else {
      verifyMsg.textContent = data.message || 'Invalid code';
      verifyMsg.className = 'msg error';
    }
  } catch (e) {
    verifyMsg.textContent = 'Network error';
    verifyMsg.className = 'msg error';
  }
});
