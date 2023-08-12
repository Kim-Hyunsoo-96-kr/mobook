import '../App.css';
import LoginSection from "../comp/LoginSection";
import Section2 from "../comp/Section2";
import axios from "axios";
import {axiosInstance, CONFIG, isLoginedSelector, setLogin, Toast, Toast2} from "../recoil";
import {useRecoilValue} from "recoil";
import {Navigate, useNavigate} from "react-router-dom";
import Swal from "sweetalert2";

const FindPassword = () => {
    const navigate = useNavigate();
    const isLogined = useRecoilValue(isLoginedSelector);
    if (isLogined) return <Navigate to="/" replace />;
    const onSubmit = async (event) => {
        event.preventDefault();

        const form = event.target;

        form.email.value = form.email.value.trim();
        form.name.value = form.name.value.trim();

        if (form.email.value.length === 0) {
            Swal.fire(
                '이메일을 입력해주세요',
                '이메일로 새로운 비밀번호가 전송됩니다.',
                'warning'
            )
            form.email.focus();
            return;
        }

        if (form.name.value.length === 0) {
            Swal.fire(
                '이름을 입력해주세요',
                '이름은 필수입니다.',
                'warning'
            )
            form.name.focus();
            return;
        }

        const email = form.email.value;
        const name = form.name.value;

        try {
            // 로딩 상태 시작
            Toast2.fire({
                icon: 'info',
                title: '작업 중...'
            });

            const response = await axios.post(CONFIG.API_FIND_PW, {email, name});
            navigate("/login", {replace: true});

            // 로딩 상태 종료
            Toast.fire({
                icon: 'success',
                title: response.data.message
            });
        } catch (error) {
            // 에러 처리 및 로딩 상태 종료
            if(error.response.status === 400){
                Swal.fire(
                    error.response.data.message,
                    '다시 확인해주세요.',
                    'warning'
                )
            } else {
                Swal.fire(
                    '예상치 못한 에러',
                    '송주환 사원에게 문의해주세요.',
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
                            <h1 className="fw-bolder">비밀번호를 모르시나요?</h1>
                            <p className="lead fw-normal text-muted mb-0"></p>
                        </div>
                        <div className="row gx-5 justify-content-center">
                            <div className="col-lg-8 col-xl-6">
                                <form onSubmit={onSubmit}>
                                    <div className="form-floating mb-3">
                                        <input className="form-control" name='email'/>
                                        <label>이메일</label>
                                    </div>
                                    <div className="form-floating mb-3">
                                        <input className="form-control" name='name'/>
                                        <label>이름</label>
                                    </div>
                                    <div className="d-grid">
                                        <button className="btn btn-primary btn-lg" id="submitButton" type="submit" >새로운 비밀번호 발송</button>
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

export default FindPassword;
