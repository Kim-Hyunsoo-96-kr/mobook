import '../App.css';
import Header from "../comp/Header";
import {useRecoilValue, useSetRecoilState} from "recoil";
import {CONFIG, isLoginedSelector, loginedUserInfoAtom, loginedUserInfoSelector} from "../recoil";
import {Navigate} from "react-router-dom";
import axios from "axios";

function Test() {
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    const isLogined = useRecoilValue(isLoginedSelector);
    if(!isLogined) return <Navigate to={"/"}/>;
    console.log(loginedUserInfo.accessToken)
    async function getData() {
        try {
            //응답 성공
            const response = await axios.get(CONFIG.TEST,{headers : {
                Authorization: `Bearer ${loginedUserInfo.accessToken}`
            }});
            console.log(response);
        } catch (error) {
            //응답 실패
            console.error(error);
        }
    }
    return (
        <div>
            <Header/>
        </div>
    );
}

export default Test;
