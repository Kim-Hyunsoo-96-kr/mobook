import {atom, selector} from "recoil";
import {recoilPersist} from "recoil-persist";
import axios from "axios";
import {QueryClient} from "react-query";
import jwtDecode from "jwt-decode";

// 리액트 쿼리(useEffect 없이 API 통신 가능하도록, API 통신 쉽게 해줌)
export const queryClient = new QueryClient();
// 액시오스(통신 API, fetch 를 써도 되지만, access_key, refresh_key 관련 인터셉터 기능을 사용하기 위해)
export const axiosInstance = axios.create()
// 리액트 리코일 퍼시스트(전역변수 중 일부를 영속적으로 로컬 스토리지에 저장, 로그인 유지에 쓰임)
export const { persistAtom } = recoilPersist();

// 일반 lib 시작

// 어떠한 시각이 현재로 부터 몇초 남았는지
function secondsDiffFromNow(exp) {
    const diffMillis = parseInt(exp + "000") - new Date().getTime();
    return parseInt(diffMillis / 1000);
}
// jwt lib 시작
const base64UrlDecode = (input) => {
    const padding = '='.repeat((4 - (input.length % 4)) % 4);
    const base64 = (input + padding).replace(/-/g, '+').replace(/_/g, '/');
    const decoded = atob(base64);

    // 디코드된 문자열을 UTF-8 형식으로 변환
    const utf8Array = new Uint8Array(decoded.length);
    for (let i = 0; i < decoded.length; i++) {
        utf8Array[i] = decoded.charCodeAt(i);
    }

    // UTF-8 형식의 문자열을 JavaScript 문자열로 변환
    const utf8decoder = new TextDecoder();
    return utf8decoder.decode(utf8Array);
};
// 토큰에서 페이로드(데이터) 부분 가져오기
export function getPayloadFromJWT(token) {
    const base64Payload = token.split(".")[1];
    const decodedPayload = base64UrlDecode(base64Payload);
    return JSON.parse(decodedPayload);
}

// 토큰의 페이로드 부분에서 만료시간 가져오기
export function getPayloadFromJWTExp(token) {
    // const base64Payload = atob(token.split(".")[1]);
    // return base64Payload.split('"exp":')[1].split(',"')[0];
    const decodedToken = jwtDecode(token);
    return decodedToken.exp;
}

export function needToRefreshAccessToken(token) {
    try{
        const exp = getPayloadFromJWTExp(token);
        const currentTimestamp = Math.floor(Date.now() / 1000); // 현재 시간(epoch 시간) 획득

        // 만료 시간이 현재 시간으로부터 1분 이내이면 true 반환, 그렇지 않으면 false 반환
        return exp - currentTimestamp <= 60;
    } catch (e) {
        // 디코딩에 실패하거나 유효하지 않은 토큰인 경우 false 반환
        return false;
    }
}

// 리프레시 토큰을 재발급(리프레시) 해야하는지 체크
export function needToRefreshRefreshToken(token) {
    try{
        const exp = getPayloadFromJWTExp(token);
        const currentTimestamp = Math.floor(Date.now() / 1000); // 현재 시간(epoch 시간) 획득

        // 만료 시간이 현재 시간으로부터 1분 이내이면 true 반환, 그렇지 않으면 false 반환
        return exp - currentTimestamp <= 60;
    } catch (e) {
        // 디코딩에 실패하거나 유효하지 않은 토큰인 경우 false 반환
        return false;
    }
}
export const nav = {
    title: "MOBOOK1.0",
    menu: [
        {title: "홈", router: "/"},
        {title: "책 검색", router: "/search?searchText=&page=1"},
        {title: "책 요청", router: "/request"},
    ]
}
export const CONFIG = {};
CONFIG.BASE_URL = "http://localhost:8080";
CONFIG.API_LOGIN = `${CONFIG.BASE_URL}/api/members/login`;
CONFIG.API_REFRESH_TOKEN = `${CONFIG.BASE_URL}/api/members/refreshToken`;
CONFIG.API_LOGOUT = `${CONFIG.BASE_URL}/api/members/logout`;
CONFIG.API_BOOK_LIST = `${CONFIG.BASE_URL}/api/books/list`;
CONFIG.API_BOOK_SEARCH = `${CONFIG.BASE_URL}/api/books/search`;
CONFIG.API_ADD_BOOK = `${CONFIG.BASE_URL}/api/books/add`;
CONFIG.API_UPLOAD_EXCEL = `${CONFIG.BASE_URL}/api/books/add/excel`;
CONFIG.API_BOOK_RENT = `${CONFIG.BASE_URL}/api/books/rent/`;
CONFIG.API_BOOK_RETURN = `${CONFIG.BASE_URL}/api/books/return/`;
CONFIG.API_BOOK_RECOMMEND = `${CONFIG.BASE_URL}/api/books/recommend/`;
CONFIG.API_BOOK_RECOMMEND_CANCEL = `${CONFIG.BASE_URL}/api/books/recommend/cancel/`;
CONFIG.API_MYBOOK = `${CONFIG.BASE_URL}/api/members/myBook`;
CONFIG.API_MYBOOKLOG = `${CONFIG.BASE_URL}/api/members/myBookLog`;
CONFIG.API_MYRECOMMENDBOOK = `${CONFIG.BASE_URL}/api/members/myRecommendBook`;
CONFIG.API_MYRENTBOOK = `${CONFIG.BASE_URL}/api/members/myRentBook`;
CONFIG.API_REQUEST = `${CONFIG.BASE_URL}/api/books/request`;
CONFIG.TEST = `${CONFIG.BASE_URL}/api/books/list`;
// 리코일 atom, selector 시작
// 로그인한 회원정보(raw)
export const loginedUserInfoAtom = atom({
    key: "app/loginedUserInfoAtom", // 이 키는 나중에 디버깅시에 의미가 있음
    default: null, // 기본값
    effects_UNSTABLE: [persistAtom] // 이 변수의 값은 로컬 스토리지에 영속적으로 저장, 이렇게 해야 F5 키 눌러도 로그인 유지 가능
});
// loginedUserInfoAtom 를 기초로 현재 로그인 했는지 알려주는 변수
export const isLoginedSelector = selector({
    key: "app/isLoginedSelector",
    get: ({ get }) => get(loginedUserInfoAtom) != null
});
// loginedUserInfoAtom 를 사용하기 좋게 살짝 가공한 버전(앱에서는 이걸 사용한다.)
export const loginedUserInfoSelector = selector({
    key: "app/loginedUserInfoSelector",
    get: ({ get }) => {
        const isLogined = get(isLoginedSelector);

        if (!isLogined) return null; // 로그인 안했다면 null 반환

        const rawLoginedUserInfo = get(loginedUserInfoAtom);

        return {
            ...rawLoginedUserInfo
        };
    }
});

export function setLogin(setLoginedUserInfo, accessToken, refreshToken) {
    const userInfo = getPayloadFromJWT(accessToken);
    userInfo.accessToken = accessToken; // 이 토큰과
    userInfo.refreshToken = refreshToken; // 이 토큰은 꼭 있어야 로그인 했다고 인식됨
    setLoginedUserInfo(userInfo);
}

export function setLogout(setLoginedUserInfo) {
    setLoginedUserInfo(null);
}