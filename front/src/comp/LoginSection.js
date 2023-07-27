import axios from "axios";
import {atom, useRecoilState} from "recoil";
import {recoilPersist} from "recoil-persist";
import {Link, Navigate} from "react-router-dom";

// 리코일 atom, selector 시작
// 리코일 atom와 selector는 전역변수 정도로 해석하면 됩니다.
const { persistAtom } = recoilPersist();

// 일반 LIB
function getPayloadFromJWT(token) {
    const base64Payload = token.split(".")[1];
    return JSON.parse(atob(base64Payload));
}
// 로그인한 회원정보(raw)
const loginedUserInfoAtom = atom({
    key: "app/loginedUserInfoAtom", // 이 키는 나중에 디버깅시에 의미가 있음
    default: null, // 기본값
    effects_UNSTABLE: [persistAtom] // 이 변수의 값은 로컬 스토리지에 영속적으로 저장, 이렇게 해야 F5 키 눌러도 로그인 유지 가능
});
function setLogin(setLoginedUserInfo, accessToken, refreshToken) {
    const userInfo = getPayloadFromJWT(accessToken);
    userInfo.accessToken = accessToken; // 이 토큰과
    userInfo.refreshToken = refreshToken; // 이 토큰은 꼭 있어야 로그인 했다고 인식됨
    setLoginedUserInfo(userInfo);
}

function LoginSection() {
    const CONFIG = {};
    CONFIG.BASE_URL = "http://localhost:8080";
    CONFIG.API_LOGIN = "http://localhost:8080/api/members/login";
    const [loginedUserInfo, setLoginedUserInfo] = useRecoilState(loginedUserInfoAtom);
    const isLogined = loginedUserInfo != null
    if(isLogined) return <Navigate to={"/"}/>;
    const onSubmit = async (event) => {
        event.preventDefault();

        const form = event.target;

        form.email.value = form.email.value.trim();
        form.password.value = form.password.value.trim();

        if (form.email.value.length === 0) {
            alert("email을 입력해주세요.");
            form.email.focus();
            return;
        }

        if (form.password.value.length === 0) {
            alert("password를 입력해주세요.");
            form.password.focus();
            return;
        }

        const email = form.email.value;
        const password = form.password.value;

        try {
            const response = await axios.post(CONFIG.API_LOGIN, {email, password});
            setLogin(setLoginedUserInfo, response.data.accessToken, response.data.refreshToken)
            alert("로그인 성공")
        }
        catch (e) {
            if(e.response.status == 400){
                alert("비밀번호가 일치하지 않습니다.")
                form.email.focus()
            } else if(e.response.status == 500){
                alert("일치하는 아이디가 없습니다.")
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
                            <form id="contactForm" onSubmit={onSubmit}>
                                <div className="form-floating mb-3">
                                    <input className="form-control" id="email" type="email" name='email'
                                           placeholder="name@example.com"/>
                                    <label htmlFor="email">Email address</label>
                                    <div className="invalid-feedback" data-sb-feedback="email:required">An email is
                                        required.
                                    </div>
                                    <div className="invalid-feedback" data-sb-feedback="email:email">Email is not
                                        valid.
                                    </div>
                                </div>
                                <div className="form-floating mb-3">
                                    <input className="form-control" id="phone" type="tel" placeholder="(123) 456-7890"
                                           name='password'/>
                                    <label htmlFor="password">Password</label>
                                </div>
                                <div className="d-grid">
                                    <button className="btn btn-primary btn-lg" id="submitButton"
                                            type="submit" >Submit
                                    </button>
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
