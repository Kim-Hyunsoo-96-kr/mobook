import {useState} from "react";
import {axiosInstance, CONFIG, isLoginedSelector, loginedUserInfoSelector} from "../recoil";
import {useRecoilValue} from "recoil";
import {Navigate, useNavigate} from "react-router-dom";
import {useQuery} from "react-query";

function AddComp() {
    const navigate = useNavigate();
    const [file,setFile] = useState()
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    const FileUpload = async () => {
        const formData = new FormData();
        formData.append('testFile', file)
        const response = await axiosInstance.post(CONFIG.API_UPLOAD_EXCEL, formData);
        navigate("/search", {replace: true});
        alert("DB 저장 성공");
    }
    if (!isLogined) return <Navigate to="/" replace />; // 로그인 안했다면 메인화면으로 보냄
    const fileChangedHandler = (event)=>{
        event.preventDefault()
        const formData = new FormData();
        if(event.target.files){
            const uploadFile = event.target.files[0]
            formData.append('file',uploadFile)
            setFile(uploadFile)
        }
    }

    return (
        <section className="bg-light py-5">
            <div className="container px-5 my-5">
                <div className="text-center mb-5">
                    <h1 className="fw-bolder">책 추가</h1>
                </div>
                <div className="row gx-5 justify-content-center">
                    <div className="col-lg-6 col-xl-6">
                        <div className="card mb-5 mb-xl-0">
                            <div className="card-body p-5">
                                <div className="mb-3">
                                    <span className="display-4 fw-bold">직접 입력</span>
                                </div>
                                <div className="row gx-5 justify-content-center">
                                    <div className="margin-top30">
                                        <form id="contactForm">
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
                    </div>
                    <div className="col-lg-6 col-xl-4">
                        <div className="card mb-5 mb-xl-0">
                            <div className="card-body p-5">
                                <div className="mb-3">
                                    <span className="display-4 fw-bold">엑셀 파일</span>
                                </div>
                                <div className="margin-top30">
                                    <input type="file" onChange={fileChangedHandler}/>
                                </div>
                                <button className="btn btn-primary btn-lg" onClick={FileUpload}>등록</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default AddComp;
