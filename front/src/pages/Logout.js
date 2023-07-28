import '../App.css';
import LoginSection from "../comp/LoginSection";
import Section2 from "../comp/Section2";
import {Navigate} from "react-router-dom";
import {loginedUserInfoAtom, setLogout} from "../recoil";
import {useSetRecoilState} from "recoil";

function Logout() {
    const setLoginedUserInfo = useSetRecoilState(loginedUserInfoAtom);

    setLogout(setLoginedUserInfo);

    return <Navigate to="/" replace/>;
}

export default Logout;
