import '../App.css';
import Header from "../comp/Header";
import {useRecoilValue, useSetRecoilState} from "recoil";
import {
    axiosInstance,
    CONFIG,
    isLoginedSelector,
    loginedUserInfoAtom,
    loginedUserInfoSelector,
    queryClient, Toast
} from "../recoil";
import {Link, Navigate, useLocation, useNavigate, useParams} from "react-router-dom";
import axios from "axios";
import {useQuery} from "react-query";
import Pagination from "react-js-pagination";
import MDEditor from '@uiw/react-md-editor';
import Swal from "sweetalert2";


const NoticeDetail = () => {
    const { noticeId } = useParams();
    const navigate = useNavigate();
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    const { isLoading, error, data } = useQuery(["noticeDetail"], async () => {
        const response = await axiosInstance.get(`${CONFIG.API_NOTICE}${noticeId}`);
        return response.data;
    });
    const editNotice = () => {

    }
    const deleteNotice = () => {
        Swal.fire({
            title: '공지사항을 삭제하시겠습니까?',
            text: "삭제한 공지사항은 돌아오지 않습니다.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: '삭제',
            cancelButtonText: '취소',
            reverseButtons: true, // 버튼 순서 거꾸로
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const response = await axiosInstance.post(`${CONFIG.API_NOTICE_DELETE}${noticeId}`);
                    Toast.fire({
                        icon: 'success',
                        title: response.data.message
                    })
                    navigate("/notice", { replace: true });


                } catch (e) {
                    if (e.response.status == 400)
                        Swal.fire(
                            e.response.data.message,
                            '한번 더 확인해주세요.',
                            'warning'
                        )
                    else
                        Swal.fire(
                            '예상치 못한 오류',
                            e.message,
                            'warning'
                        )
                }
            }
        })
    }

    if (!isLogined) return <Navigate to="/login" replace />; // 로그인 안했다면 메인화면으로 보냄
    let isAdmin = null
    if(isLogined) isAdmin = loginedUserInfo.isAdmin
    if (isLoading) {
        return <div class="loading-1">로딩중</div>;
    }

    if (error) {
        return <div class="error-1">{error.message}</div>;
    }

    return (
        <section class="bg-light py-5">
            <div class="container px-5 my-5">
                <div class="text-center mb-5">
                    <h1 class="fw-bolder">공지사항</h1>
                </div>
                <div class="gx-5 justify-content-center">
                    <div class="col-lg-12 col-xl-12">
                        <div class="card mb-5">
                            <div class="card-body p-5">
                                <div class="mb-3 d-flex justify-content-between">
                                    <span class="text-muted fs-4">공지사항</span>
                                </div>
                                <div className="markdownDiv" data-color-mode="light" style={{padding:15}}>
                                    <MDEditor.Markdown
                                        style={{ padding: 10 }}
                                        source={data.notice.contents}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="float-end mb-5">
                        <button className="btn btn-outline-info btn-sm" onClick={() => editNotice(data.notice.noticeId)}>수정</button>
                        <button className="btn btn-outline-danger btn-sm mx-3" onClick={() => deleteNotice()}>삭제</button>
                        <Link to={'/notice'} className="btn btn-outline-secondary btn-sm">목록으로</Link>
                    </div>

                </div>
            </div>
        </section>
    );
}

export default NoticeDetail;
