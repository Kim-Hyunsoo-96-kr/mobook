import axios from "axios";
import {CONFIG, isLoginedSelector, loginedUserInfoAtom, setLogin} from "../recoil";
import {useRecoilValue, useSetRecoilState} from "recoil";
import {Link, Navigate} from "react-router-dom";
import Swal, {fire} from "sweetalert2";

const LoginSection = () => {
    const setLoginedUserInfo = useSetRecoilState(loginedUserInfoAtom);
    const isLogined = useRecoilValue(isLoginedSelector);
    const Toast = Swal.mixin({
        toast: true,
        position: 'top-right',
        showConfirmButton: false,
        timer: 1000,
        timerProgressBar: true,
    })
    if (isLogined) return <Navigate to="/" replace />;

    const onSubmit = async (event) => {
        event.preventDefault();

        const form = event.target;

        form.email.value = form.email.value.trim();
        form.password.value = form.password.value.trim();

        if (form.email.value.length === 0) {
            Swal.fire(
                '이메일을 입력해주세요',
                '아이디는 이메일 형식입니다.',
                'warning'
            )
            form.email.focus();
            return;
        }

        if (form.password.value.length === 0) {
            Swal.fire(
                '비밀번호를 입력해주세요',
                '비밀번호가 기억나지 않으시면 로그인 버튼 아래 링크를 클릭해주세요.',
                'warning'
            )
            form.password.focus();
            return;
        }

        const email = form.email.value;
        const password = form.password.value;

        try {
            const response = await axios.post(CONFIG.API_LOGIN, {email, password});
            setLogin(setLoginedUserInfo, response.data.accessToken, response.data.refreshToken)
            Toast.fire({
                icon: 'success',
                title: '로그인 성공'
            })
        }
        catch (e) {
            if(e.response.status == 400){
                Swal.fire(
                    e.response.data.message,
                    '다시 입력해주세요.',
                    'warning'
                )
            } else if(e.response.status == 500){
                Swal.fire(
                    '서버에러 : 요청값 에러',
                    '다시 입력해주세요.',
                    'warning'
                )
                form.password.focus()
            } else {
                alert("내가 예상하지 못한 오류")
            }
        }
    };
    return (
        <section className="py-5">
            <div className="container px-5">
                <div className="bg-light rounded-3 py-5 px-4 px-md-5 mb-5">
                    <div className="text-center mb-5">
                        <div className="feature bg-primary bg-gradient text-white rounded-3 mb-3"><i
                            className="bi bi-envelope"></i></div>
                        <h1 className="fw-bolder">Login</h1>
                        <p className="lead fw-normal text-muted mb-0"></p>
                    </div>
                    <div className="row gx-5 justify-content-center">
                        <div className="col-lg-8 col-xl-6">
                            <form onSubmit={onSubmit}>
                                <div className="form-floating mb-3">
                                    <input className="form-control" id="email" type="email" name='email'/>
                                    <label htmlFor="email">Email address</label>
                                </div>
                                <div className="form-floating mb-3">
                                    <input className="form-control" id="phone" type="password" name='password'/>
                                    <label htmlFor="password">Password</label>
                                </div>
                                <div className="d-grid">
                                    <button className="btn btn-primary btn-lg" id="submitButton"
                                            type="submit" >Submit
                                    </button>
                                <Link to="/findPassword" type="button" className="btn btn-link">비밀번호를 모르시나요?</Link>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default LoginSection;
