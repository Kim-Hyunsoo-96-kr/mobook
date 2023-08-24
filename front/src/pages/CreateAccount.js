import '../App.css';
import LoginSection from "../comp/LoginSection";
import Section2 from "../comp/Section2";
import axios from "axios";
import {axiosInstance, CONFIG, isLoginedSelector, loginedUserInfoSelector, setLogin} from "../recoil";
import {useRecoilValue} from "recoil";
import {Navigate, useNavigate} from "react-router-dom";
import Swal, {fire} from "sweetalert2";

const CreateAccount = () => {
    const isLogined = useRecoilValue(isLoginedSelector);
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    const navigate = useNavigate();
    if (!isLogined) return <Navigate to="/login" replace />;
    let isAdmin = null
    if(isLogined) isAdmin = loginedUserInfo.isAdmin
    if (!isAdmin) return <Navigate to="/" replace />;

    const onSubmit = async (event) => {
        event.preventDefault();

        const form = event.target;

        form.name.value = form.name.value.trim();
        form.email.value = form.email.value.trim();

        if (form.name.value.length === 0) {
            Swal.fire(
                '이름은 필수입니다.',
                '다시 입력해주세요',
                'warning'
            )
            form.name.focus();
            return;
        }

        if (form.email.value.length === 0) {
            Swal.fire(
                '아이디/이메일은 필수입니다.',
                '다시 입력해주세요',
                'warning'
            )
            form.email.focus();
            return;
        }

        const name = form.name.value;
        const email = form.email.value;
        const password = "qwer1234!"

        try{
            const response = await axiosInstance.post(CONFIG.API_CREATE_ACCOUNT, {name, email, password});
            if(response.status == 201){
                Swal.fire(
                    '계정이 생성되었습니다.',
                    '초기 비밀번호는 \'qwer1234!\'입니다.',
                    'success'
                )
            }
            navigate("/login", {replace: true});

        } catch (e){
            if(e.response.status == 400){
                Swal.fire(
                    '이름과 아이디를 확인해주세요.',
                    '아이디는 이메일 형식입니다. <br/>이름은 100글자 이하입니다.',
                    'success'
                )
            }
            else if(e.response.status == 412)
                Swal.fire(
                    '관리자가 아닙니다.',
                    '해당 기능은 관리자만 사용이 가능합니다.',
                    'warning'
                )
            else if(e.response.status == 500){
                Swal.fire(
                    '예상치 못한 오류',
                    '<b style="color: red">중복되는 아이디인지 한번 더 확인해주세요.</b> <br/><br/> 이 에러가 반복되면 송주환 사원에게 문의해주세요.',
                    'warning'
                )
            }
            else
                Swal.fire(
                    '예상치 못한 오류',
                    '이 에러가 반복되면 송주환 사원에게 문의해주세요.',
                    'warning'
                )

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
                              <h1 className="fw-bolder">계정 만들기</h1>
                              <p className="lead fw-normal text-muted mb-0"></p>
                          </div>
                          <div className="row gx-5 justify-content-center">
                              <div className="col-lg-8 col-xl-6">
                                  <form onSubmit={onSubmit}>
                                      <div className="form-floating mb-3">
                                          <input className="form-control" name='name'/>
                                          <label>이름</label>
                                      </div>
                                      <div className="form-floating mb-3">
                                          <input className="form-control" name='email'/>
                                          <label>아이디/이메일</label>
                                      </div>
                                      <div className="d-grid">
                                          <button className="btn btn-primary btn-lg" id="submitButton" type="submit" >생성하기</button>
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

export default CreateAccount;
