import '../App.css';
import LoginSection from "../comp/LoginSection";
import Section2 from "../comp/Section2";
import axios from "axios";
import {axiosInstance, CONFIG, isLoginedSelector, setLogin, Toast} from "../recoil";
import {useRecoilValue} from "recoil";
import {Navigate, useNavigate} from "react-router-dom";
import Swal from "sweetalert2";

const PasswordChange = () => {
    const isLogined = useRecoilValue(isLoginedSelector);
    const navigate = useNavigate();
    if (!isLogined) return <Navigate to="/login" replace />;
    const onSubmit = async (event) => {
        event.preventDefault();

        const form = event.target;

        form.oldPassword.value = form.oldPassword.value.trim();
        form.newPassword.value = form.newPassword.value.trim();
        form.checkNewPassword.value = form.checkNewPassword.value.trim();

        if (form.oldPassword.value.length === 0) {
            Swal.fire(
                '기존 비밀번호를 입력해주세요',
                '기존 비밀번호는 필수입니다.',
                'warning'
            )
            form.oldPassword.focus();
            return;
        }

        if (form.newPassword.value.length === 0) {
            Swal.fire(
                '새로운 비밀번호를 입력해주세요',
                '새로운 비밀번호는 필수입니다.',
                'warning'
            )
            form.newPassword.focus();
            return;
        }

        if (form.checkNewPassword.value.length === 0) {
            Swal.fire(
                '새로운 비밀번호 확인을 입력해주세요',
                '새로운 비밀번호 확인은 필수입니다.',
                'warning'
            )
            form.checkNewPassword.focus();
            return;
        }
        const oldPassword = form.oldPassword.value;
        const newPassword = form.newPassword.value;
        const checkNewPassword = form.checkNewPassword.value;

        try{
            const response = await axiosInstance.post(CONFIG.API_CHANGE_PW, {oldPassword, newPassword, checkNewPassword});
            navigate("/", {replace: true});
            Toast.fire({
                icon: 'success',
                title: '비밀번호 변경 성공'
            })
        } catch(e) {
            if(e.response.status == 400){
                Swal.fire(
                    e.response.data.message,
                    '다시 입력해주세요.',
                    'warning'
                )
            }
        }

    }
    return (
        <div>
            <section className="py-5">
                <div className="container px-5">
                    <div className="bg-light rounded-3 py-5 px-4 px-md-5 mb-5">
                        <div className="text-center mb-5">
                            <div className="feature bg-primary bg-gradient text-white rounded-3 mb-3"><i
                                className="bi bi-envelope"></i></div>
                            <h1 className="fw-bolder">비밀번호 변경</h1>
                            <p className="lead fw-normal text-muted mb-0"></p>
                        </div>
                        <div className="row gx-5 justify-content-center">
                            <div className="col-lg-8 col-xl-6">
                                <form onSubmit={onSubmit}>
                                    <div className="form-floating mb-3">
                                        <input className="form-control" name='oldPassword' type='password'/>
                                        <label>기존 비밀번호</label>
                                    </div>
                                    <div className="form-floating mb-3">
                                        <input className="form-control" name='newPassword' type='password'/>
                                        <label>새로운 비밀번호</label>
                                    </div>
                                    <div className="form-floating mb-3">
                                        <input className="form-control" name='checkNewPassword' type='password'/>
                                        <label>새로운 비밀번호 확인</label>
                                    </div>
                                    <div className="d-grid">
                                        <button className="btn btn-primary btn-lg" id="submitButton" type="submit" >변경하기</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
            <Section2/>
        </div>
    );
}

export default PasswordChange;
