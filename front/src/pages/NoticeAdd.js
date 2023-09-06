import React, {useState} from "react";

import MDEditor from '@uiw/react-md-editor';
import Pagination from "react-js-pagination";
import Swal from "sweetalert2";
import {axiosInstance, CONFIG, isLoginedSelector, loginedUserInfoSelector, Toast, Toast2} from "../recoil";
import {Navigate, useNavigate} from "react-router-dom";
import {useRecoilValue} from "recoil";

const NoticeAdd = () => {
    const navigate = useNavigate();
    const [md, setMd] = useState("");
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
    const onSubmit = async (event) => {
        event.preventDefault();

        const form = event.target;

        form.title.value = form.title.value.trim();

        if (form.title.value.length === 0) {
            Swal.fire(
                '제목을 입력해주세요.',
                '제목은 필수입니다.',
                'warning'
            )
            form.title.focus();
            return;
        }

        if (md.length === 0) {
            Swal.fire(
                '내용을 입력해주세요.',
                '내용은 필수입니다.',
                'warning'
            )
            return;
        }

        const title = form.title.value;

        try{
            Toast2.fire({
                icon: 'info',
                title: '작업 중...'
            });
            const response = await axiosInstance.post(CONFIG.API_NOTICEADD, {title, contents : md});
            navigate("/notice", {replace: true});
            Toast.fire({
                icon: 'success',
                title: response.data.message
            });
        } catch (e){
            Swal.fire(
                '예상치 못한 오류',
                '이 에러가 반복되면 송주환 사원에게 문의해주세요.',
                'warning'
            )
        }


    }

    return (
        <section className="bg-light py-5">
            <div className="container px-5 my-5">
                <div className="text-center mb-5">
                    <h1 className="fw-bolder">공지사항</h1>
                </div>
                <div className="gx-5 justify-content-center">
                    <div className="col-lg-12 col-xl-12">
                        <form onSubmit={onSubmit}>
                            <div className="form-floating mb-3">
                                <input className="form-control" name='title'/>
                                <label>제목</label>
                            </div>
                            <div className="card mb-5">
                                <div>
                                    <MDEditor value={md} onChange={setMd} height={600}/>
                                </div>
                            </div>
                            <div className="d-grid" style={{justifyContent : 'right'}}>
                                <button className="btn btn-primary" id="submitButton"
                                        type="submit" >작성하기
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </section>
    );
};

export default NoticeAdd;