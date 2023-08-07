import '../App.css';
import LoginSection from "../comp/LoginSection";
import Section2 from "../comp/Section2";
import axios from "axios";
import {axiosInstance, CONFIG, isLoginedSelector, setLogin} from "../recoil";
import {useRecoilValue} from "recoil";
import {Navigate, useNavigate} from "react-router-dom";

const Request = () => {
    const isLogined = useRecoilValue(isLoginedSelector);
    const navigate = useNavigate();
    if (!isLogined) return <Navigate to="/" replace />;
    const onSubmit = async (event) => {
        event.preventDefault();

        const form = event.target;

        form.bookName.value = form.bookName.value.trim();

        if (form.bookName.value.length === 0) {
            alert("책 제목은 필수사항입니다.");
            form.bookName.focus();
            return;
        }

        const bookName = form.bookName.value;
        const bookPublisher = form.bookPublisher.value;
        const bookWriter = form.bookWriter.value;

        const response = await axiosInstance.post(CONFIG.API_REQUEST, {bookName, bookPublisher, bookWriter});
        navigate("/", {replace: true});
        alert("책을 성공적으로 요청했습니다.")

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
                                          <input className="form-control" name='bookPublisher'/>
                                          <label>출판사</label>
                                      </div>
                                      <div className="form-floating mb-3">
                                          <input className="form-control" name='bookWriter'/>
                                          <label>책 저자</label>
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
