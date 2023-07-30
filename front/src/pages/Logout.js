import '../App.css';
import LoginSection from "../comp/LoginSection";
import Section2 from "../comp/Section2";
import {Navigate} from "react-router-dom";
import {CONFIG, loginedUserInfoAtom, loginedUserInfoSelector, setLogout} from "../recoil";
import {useRecoilValue, useSetRecoilState} from "recoil";
import axios from "axios";

function Logout() {
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    const setLoginedUserInfo = useSetRecoilState(loginedUserInfoAtom);
    async function getData() {
        try {
            //응답 성공
            const response = await axios.post(CONFIG.API_LOGOUT,
                {
                refreshToken: loginedUserInfo.refreshToken
                },
                {
                    headers : {
                    Authorization: `Bearer ${loginedUserInfo.accessToken}`
                    }
                });
            alert("로그아웃 성공")
        } catch (error) {
            //응답 실패
            console.error(error);
        }
    }
    getData()
    setLogout(setLoginedUserInfo);

    return <Navigate to="/" replace/>;
}

export default Logout;
