import '../App.css';
import Header from "../comp/Header";
import {useRecoilValue, useSetRecoilState} from "recoil";
import {axiosInstance, CONFIG, isLoginedSelector, loginedUserInfoAtom, loginedUserInfoSelector} from "../recoil";
import {Link, Navigate, useLocation, useNavigate, useParams} from "react-router-dom";
import axios from "axios";
import {useQuery} from "react-query";
import Pagination from "react-js-pagination";
import MDEditor from '@uiw/react-md-editor';


const NoticeDetail = () => {
    const { noticeId } = useParams();
    const navigate = useNavigate();
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    const { isLoading, error, data } = useQuery(["noticeDetail"], async () => {
        const response = await axiosInstance.get(`${CONFIG.API_NOTICE}${noticeId}`);
        return response.data;
    });
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
                </div>
            </div>
        </section>
    );
}

export default NoticeDetail;
