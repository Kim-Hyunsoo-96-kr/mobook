import {useState} from "react";
import axios from "axios";
import {CONFIG, isLoginedSelector, loginedUserInfoSelector} from "../recoil";
import {useRecoilValue} from "recoil";

function AddComp() {
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    const isLogined = useRecoilValue(isLoginedSelector);
    const [file,setFile] = useState()
    const fileChangedHandler = (event)=>{
        event.preventDefault()
        const formData = new FormData();
        if(event.target.files){
            const uploadFile = event.target.files[0]
            formData.append('file',uploadFile)
            setFile(uploadFile)
            console.log(uploadFile)
            console.log('===useState===')
            console.log(file)
        }
        console.log(file)
    }

    console.log(file)
    const fileUpload = (event) => {
        event.preventDefault()
        const formData = new FormData();
        formData.append('testFile',file)
        try {
            const config = {
                headers: {
                    Authorization: `Bearer ${loginedUserInfo.accessToken}`}
            }
            //응답 성공
            axios.post(CONFIG.API_UPLOAD_EXCEL, formData,config)
        } catch (error) {
            //응답 실패
            console.error(error);
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
                                <button className="btn btn-primary btn-lg" onClick={fileUpload}>등록</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default AddComp;