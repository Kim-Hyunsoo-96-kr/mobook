import {atom, selector} from "recoil";
import {recoilPersist} from "recoil-persist";

export const CONFIG = {};
CONFIG.BASE_URL = "http://localhost:8080";
CONFIG.API_LOGIN = `${CONFIG.BASE_URL}/api/members/login`;
CONFIG.API_LOGOUT = "http://localhost:8080/api/members/logout";
CONFIG.API_BOOK_LIST = "http://localhost:8080/api/books/list";
CONFIG.API_UPLOAD_EXCEL = "http://localhost:8080/api/books/test";
CONFIG.TEST = "http://localhost:8080/api/books/list";

export const { persistAtom } = recoilPersist();
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

export function getPayloadFromJWT(token) {
    const base64Payload = token.split(".")[1];
    return JSON.parse(atob(base64Payload));
}
export function setLogin(setLoginedUserInfo, accessToken, refreshToken) {
    const userInfo = getPayloadFromJWT(accessToken);
    userInfo.accessToken = accessToken; // 이 토큰과
    userInfo.refreshToken = refreshToken; // 이 토큰은 꼭 있어야 로그인 했다고 인식됨
    setLoginedUserInfo(userInfo);
}

export function setLogout(setLoginedUserInfo) {
    setLoginedUserInfo(null);
}