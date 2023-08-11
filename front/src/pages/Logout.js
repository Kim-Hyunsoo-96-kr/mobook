import '../App.css';
import LoginSection from "../comp/LoginSection";
import {Navigate} from "react-router-dom";
import {axiosInstance, CONFIG, loginedUserInfoAtom, loginedUserInfoSelector, setLogout} from "../recoil";
import {useRecoilValue, useSetRecoilState} from "recoil";
import axios from "axios";
import Swal from "sweetalert2";

function Logout() {
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    const setLoginedUserInfo = useSetRecoilState(loginedUserInfoAtom);
    const Toast = Swal.mixin({
        toast: true,
        position: 'top-right',
        showConfirmButton: false,
        timer: 1000,
        timerProgressBar: true,
    })
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
            console.log(response);
            Toast.fire({
                icon: 'success',
                title: '로그아웃 성공'
            })
        } catch (error) {
            Swal.fire(
                '송주환 사원에게 문의해주세요',
                error.message,
                'warning'
            )
        }
    }
    getData()
    setLogout(setLoginedUserInfo);

    return <Navigate to="/" replace/>;
}

export default Logout;
