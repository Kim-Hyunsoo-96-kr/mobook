import '../App.css';
import Header from "../comp/Header";
import {useRecoilValue, useSetRecoilState} from "recoil";
import {
    axiosInstance,
    CONFIG,
    isLoginedSelector,
    loginedUserInfoAtom,
    loginedUserInfoSelector,
    queryClient
} from "../recoil";
import {Navigate, useLocation, useNavigate} from "react-router-dom";
import axios from "axios";
import {useQuery} from "react-query";
import Swal from "sweetalert2";
import Pagination from "react-js-pagination";

const MyRecommendBook = () => {
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const searchText = queryParams.get("searchText") || "";
    const page = parseInt(queryParams.get("page") || "1") - 1;
    const navigate = useNavigate();
    const { isLoading, error, data } = useQuery(["myRecommendBook", page, searchText], async () => {
        const response = await axiosInstance.get(`${CONFIG.API_MYRECOMMENDBOOK}?page=${page}&searchText=${searchText}`);
        return response.data;
    });
    const handlePageChange = (page) => {
        navigate(`/myRecommendBook?searchText=${searchText}&page=${page}`);
    };
    const submitSearch = (event) => {
        event.preventDefault();
        const form = event.target;
        form.searchText.value = form.searchText.value.trim();
        if (form.searchText.value.length === 0) {
            navigate(`/myRecommendBook?searchText=&page=1`);
        } else {
            const searchText = form.searchText.value;
            navigate(`/myRecommendBook?searchText=${searchText}&page=1`);
        }
    };
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    if (!isLogined) return <Navigate to="/login" replace />; // 로그인 안했다면 메인화면으로 보냄

    const rentBook = async (bookNumber) => {
        try{
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_RENT}${bookNumber}`);
            Swal.fire(
                response.data.message,
                '내 책 관리에서 대여 내역을 볼 수 있습니다.',
                'success'
            ).then(() => {
                queryClient.invalidateQueries(["myRecommendBook"]);
            })
        } catch (e) {
            if(e.response.status == 400)
                Swal.fire(
                    e.response.data.message,
                    '한번 더 확인해주세요.',
                    'warning'
                )
            else
                Swal.fire(
                    '예상치 못한 오류',
                    error.message,
                    'warning'
                )
        }
    }
    const returnBook = async (bookNumber) => {
        try{
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_RETURN}${bookNumber}`);
            alert("반납 성공");
        } catch (e) {
            alert("반납이 불가능한 책 입니다.")
        }
    }

    const recommendCancel = async (bookNumber) => {
        try{
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_RECOMMEND_CANCEL}${bookNumber}`);
            Swal.fire(
                response.data.message,
                '책 검색 페이지에서 추천할 수 있습니다.',
                'success'
            ).then(() => {
                queryClient.invalidateQueries(["myRecommendBook"]);
            })
        } catch (e) {
            if(e.response.status == 400)
                Swal.fire(
                    e.response.data.message,
                    '한번 더 확인해주세요.',
                    'warning'
                )
            else
                Swal.fire(
                    '예상치 못한 오류',
                    error.message,
                    'warning'
                )
        }
    }

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
                    <h1 class="fw-bolder">내 책 관리</h1>
                </div>
                <div className="search">
                    <form className="d-flex" onSubmit={submitSearch}>
                        <input className="form-control me-2" name="searchText" type="search" placeholder="책 제목 검색" aria-label="Search"/>
                        <button className="btn btn-outline-success" type="submit">Search</button>
                    </form>
                </div>
                <div class="gx-5 justify-content-center">
                    <div class="col-lg-12 col-xl-12">
                        <div class="card">
                            <div class="card-body p-5">
                                <div class="mb-3">
                                    <span class="text-muted fs-4">추천한 책</span>
                                </div>
                                <table className="table table-hover table-striped">
                                    <thead>
                                    <tr>
                                        <th className="text-align-center">번호</th>
                                        <th>제목</th>
                                        <th className="text-align-center">추천 수</th>
                                        <th className="text-align-center">대여가능여부</th>
                                        <th></th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {data.recommendBook.map((book)=>(
                                        <tr>
                                            <td className="text-align-center">{book.bookNumber}</td>
                                            <td>{book.bookName}</td>
                                            <td className="text-align-center">{book.recommend}</td>
                                            <td className="text-align-center">{book.isAble ? "Y" : "N"}</td>
                                            <td><button className="btn btn-outline-success btn-sm" onClick={() => recommendCancel(book.bookNumber)}>추천 취소하기</button></td>
                                            <td><button className="btn btn-outline-primary btn-sm" onClick={() => rentBook(book.bookNumber)}>대여신청</button></td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                                <div className="p">
                                    <Pagination
                                        activePage={page+1}
                                        itemsCountPerPage={10}
                                        totalItemsCount={data.totalCnt}
                                        pageRangeDisplayed={5}
                                        onChange={handlePageChange}>
                                    </Pagination>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default MyRecommendBook;
