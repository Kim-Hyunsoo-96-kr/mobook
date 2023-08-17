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

const AdminRentBook = () => {
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const searchText = queryParams.get("searchText") || "";
    const page = parseInt(queryParams.get("page") || "1") - 1;
    const navigate = useNavigate();
    const { isLoading, error, data } = useQuery(["myRentBook", page, searchText], async () => {
        const response = await axiosInstance.get(`${CONFIG.API_ADMIN_RENTBOOK}?page=${page}&searchText=${searchText}`);
        return response.data;
    });
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    const handlePageChange = (page) => {
        navigate(`/adminRentBook?searchText=${searchText}&page=${page}`);
    };
    const submitSearch = (event) => {
        event.preventDefault();
        const form = event.target;
        form.searchText.value = form.searchText.value.trim();
        if (form.searchText.value.length === 0) {
            navigate(`/adminRentBook?searchText=&page=1`);
        } else {
            const searchText = form.searchText.value;
            navigate(`/adminRentBook?searchText=${searchText}&page=1`);
        }
    };
    if (!isLogined) return <Navigate to="/login" replace />; // 로그인 안했다면 메인화면으로 보냄

    const extendPeriod = async (bookNumber) => {
        try{
            const response = await axiosInstance.post(`${CONFIG.API_ADMIN_EXTEND_PERIOD}${bookNumber}`);
            Swal.fire(
                response.data.message,
                '오늘 날짜로부터 2주 뒤로 연장되었습니다.',
                'success'
            ).then(() => {
                queryClient.invalidateQueries("myRentBook");
            })
        } catch (e) {
            if(e.response.status == 400 || e.response.status == 412)
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
            const response = await axiosInstance.post(`${CONFIG.API_ADMIN_RETURN_RENTBOOK}${bookNumber}`);
            Swal.fire(
                response.data.message,
                '관리자 권한으로 반납처리했습니다.',
                'success'
            ).then(() => {
                queryClient.invalidateQueries("myRentBook");
            })
        } catch (e) {
            if(e.response.status == 400 || e.response.status == 412)
                Swal.fire(
                    e.response.data.message,
                    '관리자에게 문의해주세요.',
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
                    <h1 class="fw-bolder">현재 대여 중인 책 내역</h1>
                </div>
                <div className="search">
                    <form className="d-flex" onSubmit={submitSearch}>
                        <input className="form-control me-2" name="searchText" type="search" placeholder="책 제목 검색" aria-label="Search"/>
                        <button className="btn btn-outline-success" type="submit">Search</button>
                    </form>
                </div>
                <div class="gx-5 justify-content-center">
                    <div class="col-lg-12 col-xl-12">
                        <div class="card mb-5">
                            <div class="card-body p-5">
                                <div class="mb-3">
                                    <span class="text-muted fs-4">대여중인 책</span>
                                </div>
                                <table className="table table-hover table-striped">
                                    <thead>
                                    <tr>
                                        <th className="text-align-center">번호</th>
                                        <th>제목</th>
                                        <th className="text-align-center">대여일</th>
                                        <th className="text-align-center">반납예정일</th>
                                        <th className="text-align-center">회원 이름</th>
                                        <th></th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {data.rentBook.map((book)=>(
                                        <tr>
                                            <td className="text-align-center">{book.bookNumber}</td>
                                            <td>{book.bookName}</td>
                                            <td className="text-align-center">{book.rentDate}</td>
                                            <td className="text-align-center">{book.returnDate}</td>
                                            <td className="text-align-center">{book.userName}</td>
                                            <td><button className="btn btn-outline-primary btn-sm" onClick={() => extendPeriod(book.bookNumber)}>
                                                반납기한 연장하기
                                            </button></td>
                                            <td><button className="btn btn-outline-success btn-sm" onClick={() => returnBook(book.bookNumber)}>
                                                반납하기
                                            </button></td>
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

export default AdminRentBook;
