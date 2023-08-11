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
        formData.append('excelFile', file)
        const response = await axiosInstance.post(CONFIG.API_UPLOAD_EXCEL, formData);
        navigate("/search", {replace: true});
        alert("DB 저장 성공");
    }
    const bookAdd = async (event) => {
        event.preventDefault();

        const form = event.target;

        form.bookNumber.value = form.bookNumber.value.trim();
        form.bookName.value = form.bookName.value.trim();

        if (form.bookNumber.value.length === 0) {
            alert("bookNumber 입력해주세요.");
            form.bookNumber.focus();
            return;
        }

        if (form.bookName.value.length === 0) {
            alert("bookName 입력해주세요.");
            form.bookName.focus();
            return;
        }

        const bookNumber = form.bookNumber.value;
        const bookName = form.bookName.value;
        const response = await axiosInstance.post(CONFIG.API_ADD_BOOK, {bookNumber, bookName});
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
                                    <span className="display-6 fw-bold">한 권씩 추가하기</span>
                                </div>
                                <div className="row gx-5 justify-content-center">
                                    <div className="margin-top30">
                                            <form onSubmit={bookAdd}>
                                            <div className="form-floating mb-3">
                                                <input className="form-control" id="bookNumber" name='bookNumber'/>
                                                <label htmlFor="bookNumber">책 넘버링</label>
                                            </div>
                                            <div className="form-floating mb-3">
                                                <input className="form-control" id="bookName" name='bookName'/>
                                                <label htmlFor="bookName">책 제목</label>
                                            </div>
                                            <div className="d-grid">
                                                <button className="btn btn-primary btn-lg" type="submit">책 추가</button>
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
                                    <span className="display-6 fw-bold">엑셀로 추가하기</span>
                                </div>
                                <div className="margin-top78">
                                    <input className="form-control" type="file" onChange={fileChangedHandler}/>
                                </div>
                                <div className="d-grid margin-top78">
                                    <button className="btn btn-primary btn-lg" onClick={FileUpload}>등록</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default AddComp;
