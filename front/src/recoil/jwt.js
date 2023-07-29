
export function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 어떠한 시각이 현재로 부터 몇초 남았는지
export function secondsDiffFromNow(exp) {
    const diffMillis = parseInt(exp + "000") - new Date().getTime();
    return parseInt(diffMillis / 1000);
}

// 토큰에서 페이로드(데이터) 부분 가져오기
export function getPayloadFromJWT(token) {
    const base64Payload = token.split(".")[1];
    return JSON.parse(atob(base64Payload));
}

// 토큰의 페이로드 부분에서 만료시간 가져오기
export function getPayloadFromJWTExp(token) {
    const base64Payload = atob(token.split(".")[1]);
    return base64Payload.split('"exp":')[1].split(',"')[0];
}

// 엑세스 토큰을 재발급(리프레시) 해야하는지 체크
export function needToRefreshAccessToken(token) {
    const exp = getPayloadFromJWTExp(token);
    return secondsDiffFromNow(exp) < 60 * 0;
}

// 리프레시 토큰을 재발급(리프레시) 해야하는지 체크
export function needToRefreshRefreshToken(token) {
    const exp = getPayloadFromJWTExp(token);
    return secondsDiffFromNow(exp) < 60 * 60 * 24 * 10;
}