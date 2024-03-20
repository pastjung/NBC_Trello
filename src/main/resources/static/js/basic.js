const host = 'http://' + window.location.host;
let targetId;
let folderTargetId;

$(document).ready(function () {
  const auth = getToken();

  if (auth !== undefined && auth !== '') {
    $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
      jqXHR.setRequestHeader('Authorization', auth);
    });
  } else {
    window.location.href = host + '/api/user/login-page';
    return;
  }

  // 사용자가 로그인한 상태인지 확인 + 사용자 정보를 불러와 화면에 출력
  $.ajax({
    type: 'GET',
    url: `/api/user-info`,
    contentType: 'application/json',
  })
  .done(function (res, status, xhr) {
    const username = res.username;
    const isAdmin = !!res.admin;

    if (!username) {
      window.location.href = '/api/user/login-page';
      return;
    }

    $('#username').text(username);
    if (isAdmin) {
      $('#admin').text(true);
      showPage();
    } else {
      showPage();
    }

    // 로그인한 유저의 폴더
    $.ajax({
      type: 'GET',
      url: `/api/user-folder`,
      error(error) {
        logout();
      }
    }).done(function (fragment) {
      $('#fragment').replaceWith(fragment);
    });

  })
  .fail(function (jqXHR, textStatus) {
    logout();
  });
})

function showPage(folderId = null) {
  let sorting = $("#sorting option:selected").val();
  let isAsc = $(':radio[name="isAsc"]:checked').val();

  let dataSource = `/api/folders/${folderTargetId}/products?sortBy=${sorting}&isAsc=${isAsc}`;

  $('#pagination').pagination({
    dataSource,
    locator: 'content',
    alias: {
      pageNumber: 'page',
      pageSize: 'size'
    },
    pageSize: 10,
    showPrevious: true,
    showNext: true,
  });
}

function logout() {
  // 토큰 삭제
  Cookies.remove('Authorization', {path: '/'});
  window.location.href = host + '/api/user/login-page';
}

function getToken() {

  let auth = Cookies.get('Authorization');

  if(auth === undefined) {
    return '';
  }

  // kakao 로그인 사용한 경우 Bearer 추가
  if(auth.indexOf('Bearer') === -1 && auth !== ''){
    auth = 'Bearer ' + auth;
  }

  return auth;
}


