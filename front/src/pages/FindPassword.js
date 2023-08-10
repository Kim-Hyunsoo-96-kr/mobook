import '../App.css';
import LoginSection from "../comp/LoginSection";
import Section2 from "../comp/Section2";
import axios from "axios";
import {axiosInstance, CONFIG, isLoginedSelector, setLogin} from "../recoil";
import {useRecoilValue} from "recoil";
import {Navigate, useNavigate} from "react-router-dom";

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
            alert("이메일은 필수입니다.");
            form.email.focus();
            return;
        }

        if (form.name.value.length === 0) {
            alert("이름은 필수입니다.");
            form.name.focus();
            return;
        }

        const email = form.email.value;
        const name = form.name.value;

        const response = await axios.post(CONFIG.API_FIND_PW, {email, name});
        navigate("/login", {replace: true});
        alert(response.data.message)

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
