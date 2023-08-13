import {useState} from "react";
import {axiosInstance, CONFIG, isLoginedSelector, loginedUserInfoSelector, Toast, Toast2} from "../recoil";
import {useRecoilValue} from "recoil";
import {Navigate, useNavigate} from "react-router-dom";
import {useQuery} from "react-query";
import Swal from "sweetalert2";

function AddComp() {
    const navigate = useNavigate();
    const [file,setFile] = useState(null)
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    const FileUpload = async () => {
        if(file === null){
            Swal.fire(
                '파일이 선택되지 않았습니다.',
                '파일을 선택해주세요.',
                'warning'
            )
        } else {
            const formData = new FormData();
            formData.append('excelFile', file)

            try{
                Toast2.fire({
                    icon: 'info',
                    title: '작업 중...'
                });
                const response = await axiosInstance.post(CONFIG.API_UPLOAD_EXCEL, formData);
                navigate("/search", {replace: true});
                // 로딩 상태 종료
                Toast.fire({
                    icon: 'success',
                    title: response.data.message
                });
            } catch (e){
                if(e.response.status == 400)
                Swal.fire(
                    e.response.data.message,
                    '첨부 파일을 확인해주세요.',
                    'warning'
                )
                if(e.response.status == 500)
                    Swal.fire(
                        '예상치 못한 오류',
                        '<b style="color: red">중복되는 책 번호가 없는 지 한번 더 확인해주세요.</b> <br/><br/> 이 에러가 반복되면 송주환 사원에게 문의해주세요.',
                        'warning'
                    )
            }

        }
    }
    const bookAdd = async (event) => {
        event.preventDefault();

        const form = event.target;

        form.bookNumber.value = form.bookNumber.value.trim();
        form.bookName.value = form.bookName.value.trim();

        if (form.bookNumber.value.length === 0) {
            Swal.fire(
                '책 번호를 입력해주세요.',
                '책 번호는 필수입니다.',
                'warning'
            )
            form.bookNumber.focus();
            return;
        }

        if (form.bookName.value.length === 0) {
            Swal.fire(
                '책 제목을 입력해주세요.',
                '책 제목은 필수입니다.',
                'warning'
            )
            form.bookName.focus();
            return;
        }

        const bookNumber = form.bookNumber.value;
        const bookName = form.bookName.value;

        try{
            // 로딩 상태 시작
            Toast2.fire({
                icon: 'info',
                title: '작업 중...'
            });

            const response = await axiosInstance.post(CONFIG.API_ADD_BOOK, {bookNumber, bookName});
            navigate("/search", {replace: true});

            Toast.fire({
                icon: 'success',
                title: response.data.message
            });
        } catch (e) {
            if(e.response.status == 500)
                Swal.fire(
                    '예상치 못한 오류',
                    '<b style="color: red">중복되는 책 번호가 없는 지 한번 더 확인해주세요.</b> <br/><br/> 이 에러가 반복되면 송주환 사원에게 문의해주세요.',
                    'warning'
                )
            if(e.response.status == 412)
                Swal.fire(
                    '관리자가 아닙니다.',
                    '해당 기능은 관리자만 사용이 가능합니다.',
                    'warning'
                )
            else
                Swal.fire(
                    '예상치 못한 오류',
                    '이 에러가 반복되면 송주환 사원에게 문의해주세요.',
                    'warning'
                )

        }

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
                                                <label htmlFor="bookNumber">책 번호</label>
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
                    <div className="col-lg-6 col-xl-6">
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
