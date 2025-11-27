// Script test nhanh để debug lỗi 403
// Copy và paste vào Browser Console (F12)

console.log('🔍 Bắt đầu test lỗi 403...\n');

// 1. Kiểm tra token
const token = localStorage.getItem('token');
if (!token) {
  console.error('❌ Không có token trong localStorage!');
  console.log('👉 Hãy login lại để lấy token.');
} else {
  console.log('✅ Token tồn tại:', token.substring(0, 20) + '...');
  
  // Decode token
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    console.log('\n📋 Token Payload:');
    console.log('  - Username:', payload.sub);
    console.log('  - User ID:', payload.userId);
    console.log('  - Role:', payload.role);
    
    // Kiểm tra token hết hạn
    const now = Math.floor(Date.now() / 1000);
    if (payload.exp && payload.exp < now) {
      console.error('  ❌ Token đã hết hạn!');
    } else {
      console.log('  ✅ Token còn hiệu lực');
    }
  } catch (e) {
    console.error('❌ Lỗi decode token:', e);
  }
}

// 2. Test endpoint debug
console.log('\n🔍 Testing /api/public/test-auth...');
fetch('http://localhost:8081/api/public/test-auth', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token')
  }
})
.then(r => r.json())
.then(data => {
  console.log('📋 Auth Info:', data);
  if (data.authenticated) {
    console.log('✅ User:', data.username);
    console.log('✅ Authorities:', data.authorities);
    
    // Kiểm tra authorities
    const hasUserRole = data.authorities.some(a => a === 'ROLE_USER' || a === 'ROLE_DOCTOR' || a === 'ROLE_ADMIN');
    if (hasUserRole) {
      console.log('✅ User có role hợp lệ');
    } else {
      console.error('❌ User không có role hợp lệ!');
      console.log('   Authorities:', data.authorities);
    }
  } else {
    console.error('❌ Chưa được authenticate!');
    console.log('   Message:', data.message);
  }
})
.catch(e => {
  console.error('❌ Lỗi khi test auth:', e);
});

// 3. Test endpoint patient
console.log('\n🔍 Testing /api/patient/user/3...');
fetch('http://localhost:8081/api/patient/user/3', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token')
  }
})
.then(r => {
  console.log('📋 Response Status:', r.status, r.statusText);
  if (r.status === 403) {
    console.error('❌ 403 Forbidden - Vấn đề về authorization!');
    console.log('👉 Kiểm tra:');
    console.log('   1. User có role đúng trong database?');
    console.log('   2. User có is_active = true?');
    console.log('   3. Backend logs hiển thị gì?');
  } else if (r.status === 200) {
    console.log('✅ Request thành công!');
    return r.json();
  } else {
    console.error('❌ Lỗi khác:', r.status);
    return r.text();
  }
})
.then(data => {
  if (data) {
    console.log('📋 Response Data:', data);
  }
})
.catch(e => {
  console.error('❌ Lỗi khi test patient API:', e);
});

console.log('\n✅ Test hoàn tất! Kiểm tra kết quả ở trên.');

