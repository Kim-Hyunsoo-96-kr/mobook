import '../App.css';
import Header from "../comp/Header";
import {useRecoilValue, useSetRecoilState} from "recoil";
import {CONFIG, isLoginedSelector, loginedUserInfoAtom, loginedUserInfoSelector} from "../recoil";
import {Navigate} from "react-router-dom";
import axios from "axios";
import AddComp from "../comp/AddComp";
import Swal from "sweetalert2";

function AddBook() {
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    const isLogined = useRecoilValue(isLoginedSelector);
    let isAdmin = null
    if(isLogined) isAdmin = loginedUserInfo.isAdmin
    if(!isLogined) return <Navigate to={"/login"}/>;
    if(!isAdmin) {
        Swal.fire(
            '관리자만 접근할 수 있는 페이지입니다.',
            '이 에러가 반복되면 송주환 사원에게 문의해주세요.',
            'warning'
        )
        return <Navigate to={"/"}/>;
    }
    return (
        <div>
            <AddComp/>
        </div>
    );
}

export default AddBook;
