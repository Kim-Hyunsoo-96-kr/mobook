import '../App.css';
import LoginSection from "../comp/LoginSection";
import Section2 from "../comp/Section2";
import axios from "axios";
import {axiosInstance, CONFIG, isLoginedSelector, setLogin, Toast, Toast2} from "../recoil";
import {useRecoilValue} from "recoil";
import {Navigate, useNavigate} from "react-router-dom";
import Swal from "sweetalert2";

const Request = () => {
    const isLogined = useRecoilValue(isLoginedSelector);
    const navigate = useNavigate();
    if (!isLogined) return <Navigate to="/login" replace />;
    const onSubmit = async (event) => {
        event.preventDefault();

        const form = event.target;

        form.bookName.value = form.bookName.value.trim();
        form.bookLink.value = form.bookLink.value.trim();

        if (form.bookName.value.length === 0) {
            Swal.fire(
                '책 제목을 입력해주세요.',
                '책 제목은 필수입니다.',
                'warning'
            )
            form.bookName.focus();
            return;
        }

        if (form.bookLink.value.length === 0) {
            Swal.fire(
                '책 구매링크를 입력해주세요.',
                '책 구매링크는 필수입니다.',
                'warning'
            )
            form.bookLink.focus();
            return;
        }

        const bookName = form.bookName.value;
        const bookLink = form.bookLink.value;

        try{
            Toast2.fire({
                icon: 'info',
                title: '작업 중...'
            });
            const response = await axiosInstance.post(CONFIG.API_REQUEST, {bookName, bookLink});
            navigate("/", {replace: true});
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
          <div>
              <section className="py-5">
                  <div className="container px-5">
                      <div className="bg-light rounded-3 py-5 px-4 px-md-5 mb-5">
                          <div className="text-center mb-5">
                              <div className="feature bg-primary bg-gradient text-white rounded-3 mb-3"><i
                                  className="bi bi-envelope"></i></div>
                              <h1 className="fw-bolder">책 요청</h1>
                              <p className="lead fw-normal text-muted mb-0"></p>
                          </div>
                          <div className="row gx-5 justify-content-center">
                              <div className="col-lg-8 col-xl-6">
                                  <form onSubmit={onSubmit}>
                                      <div className="form-floating mb-3">
                                          <input className="form-control" name='bookName'/>
                                          <label>책 제목</label>
                                      </div>
                                      <div className="form-floating mb-3">
                                          <input className="form-control" name='bookLink'/>
                                          <label>책 구매 링크</label>
                                      </div>
                                      <div className="d-grid">
                                          <button className="btn btn-primary btn-lg" id="submitButton"
                                                  type="submit" >요청하기
                                          </button>
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

export default Request;
