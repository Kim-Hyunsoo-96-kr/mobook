import '../App.css';
import {useRecoilValue, useSetRecoilState} from "recoil";
import {
    axiosInstance,
    CONFIG,
    isLoginedSelector, loginedUserInfoAtom, queryClient, Toast, Toast2,
} from "../recoil";
import {Link, Navigate, useLocation, useNavigate} from "react-router-dom";
import {useQuery, useQueryClient} from "react-query";
import Pagination from "react-js-pagination";
import Swal from "sweetalert2";
import {useState} from "react";
import CompComment from "../comp/CompComment";

const Search = () => {
    const [comment, setComment] = useState('');
    const handleInputChange = (event) => {
        setComment(event.target.value);
    };
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const searchText = queryParams.get("searchText") || "";
    const page = parseInt(queryParams.get("page") || "1") - 1;
    const navigate = useNavigate();
    const { isLoading, error, data } = useQuery(["bookList", page, searchText], async () => {
        const response = await axiosInstance.get(`${CONFIG.API_BOOK_SEARCH}?page=${page}&searchText=${searchText}`);
        return response.data;
    });
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    if (!isLogined) return <Navigate to="/login" replace />; // 로그인 안했다면 메인화면으로 보냄
    const handlePageChange = (page) => {
        navigate(`/search?searchText=${searchText}&page=${page}`);
    };
    const submitSearch = (event) => {
        event.preventDefault();
        const form = event.target;
        form.searchText.value = form.searchText.value.trim();
        if (form.searchText.value.length === 0) {
            navigate(`/search?searchText=&page=1`);
        } else {
            const searchText = form.searchText.value;
            navigate(`/search?searchText=${searchText}&page=1`);
        }
    };
    const rentBook = async (bookNumber) => {
        try{
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_RENT}${bookNumber}`);
            Swal.fire(
                response.data.message,
                '내 책 관리에서 대여 내역을 볼 수 있습니다.',
                'success'
            ).then(() => {
                queryClient.invalidateQueries(["bookList", page, searchText]);
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
    const heartBook = async (bookNumber) => {
        try{
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_RECOMMEND}${bookNumber}`);
            Swal.fire(
                response.data.message,
                '내 책 관리에서 찜한 책 내역을 볼 수 있습니다.',
                'success'
            ).then(() => {
                queryClient.invalidateQueries(["bookList", page, searchText]);
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
    const addComment = async (event, bookNumber) => {
        event.preventDefault();

        const form = event.target;

        form.comment.value = form.comment.value.trim();

        if (form.comment.value.length === 0) {
            Swal.fire(
                '댓글을 입력해주세요.',
                '빈 값 입니다.',
                'warning'
            )
            form.comment.focus();
            return;
        }
        const comment = form.comment.value;

        try{
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_COMMENT}${bookNumber}`, {comment});
            Toast.fire({
                icon: 'success',
                title: response.data.message
            })
            setComment('');
            queryClient.invalidateQueries(["bookList", page, searchText]);
        } catch (e) {
            console.log(e)
        }

    }
    const deleteComment = (commentId) => {
            Swal.fire({
                title: '댓글을 삭제하시겠습니까?',
                text: "삭제한 댓글은 돌아오지 않습니다.",
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
                        const response = await axiosInstance.post(`${CONFIG.API_BOOK_COMMENT_DELETE}${commentId}`);
                        Toast.fire({
                            icon: 'success',
                            title: response.data.message
                        })
                        queryClient.invalidateQueries(["bookList", page, searchText]);

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
                                error.message,
                                'warning'
                            )
                    }
                }
            })
    }

    if (isLoading) {
        return <div class="loading-1">로딩중</div>;
    }

    if (error) {
        return <div class="error-1">{error.message}</div>;
    }

    return (
        <section className="py-5">
            <div className="container px-5 my-5">
                <div className="text-center mb-5">
                    <h1 className="fw-bolder">책 목록</h1>
                </div>
                <div className="search">
                    <form className="d-flex" onSubmit={submitSearch}>
                        <input className="form-control me-2" name="searchText" type="search" placeholder="책 제목 검색" aria-label="Search"/>
                            <button className="btn btn-outline-success" type="submit">Search</button>
                    </form>
                </div>
                    <div className="container-fluid px-4">
                        <div className="card mb-4 fs-7">
                            <div className="card-body">
                                <table className="table table-hover table-striped">
                                    <thead>
                                    <tr>
                                        <th className="text-align-center">책 정보</th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                        {data.bookList.map((book)=>(
                                            <tr key={book.bookId}>
                                                <td className="text-align-center">
                                                    <img src={book.bookImageUrl} alt="Book Cover" style={{maxWidth : '180px'}} />
                                                </td>
                                                <td>
                                                    <div>
                                                        <p>번호: {book.bookNumber}</p>
                                                        <p>제목: {book.bookName}</p>
                                                        <p>입고일: {book.regDate}</p>
                                                        <p>대여 가능 여부: {book.isAble ? "가능" : "불가능"}</p>
                                                        <p>찜 수: {book.recommend}</p>
                                                        <div style={{display : 'flex', alignItems : 'center'}}>
                                                            <p><button className="btn btn-outline-success btn-sm" onClick={() => heartBook(book.bookNumber)}>
                                                                찜하기
                                                            </button></p>
                                                            {book.isAble &&
                                                            <p style={{marginLeft : '8px'}}><button className="btn btn-outline-primary btn-sm" onClick={() => rentBook(book.bookNumber)}>
                                                                대여가능
                                                            </button></p>
                                                            }
                                                            <p>
                                                                <div style={{marginLeft : '8px', display : 'flex', alignItems : 'center'}} type="button" className="bi bi-chat-dots btn btn-outline-secondary btn-sm" data-bs-toggle="modal" data-bs-target="#staticBackdrop">
                                                                <div style={{marginLeft : '4px'}}>댓글({book.bookCommentList.length})</div>
                                                                </div>
                                                                <div className="modal fade" id="staticBackdrop"
                                                                     data-bs-backdrop="static" data-bs-keyboard="false"
                                                                     tabIndex="-1" aria-labelledby="staticBackdropLabel"
                                                                     aria-hidden="true">
                                                                    <div className="modal-dialog modal-dialog-centered">
                                                                        <div className="modal-content">
                                                                            <div className="modal-header">
                                                                                <h6 className="modal-title"
                                                                                    id="staticBackdropLabel">댓글({book.bookCommentList.length})</h6>
                                                                                <button type="button"
                                                                                        className="btn-close"
                                                                                        data-bs-dismiss="modal"
                                                                                        aria-label="Close"></button>
                                                                            </div>
                                                                            <div className="modal-body">
                                                                                <section>
                                                                                    <div class="card bg-light">
                                                                                        <div class="card-body">
                                                                                            {book.bookCommentList.map((comment)=>(
                                                                                            <CompComment comment={comment} key={comment.id} />
                                                                                            ))}
                                                                                        </div>
                                                                                    </div>
                                                                                </section>
                                                                            </div>
                                                                            <form className="modal-footer" onSubmit={(event) => addComment(event, book.bookNumber)}>
                                                                                <textarea
                                                                                className="form-control mb-2"
                                                                                name='comment'
                                                                                rows="3"
                                                                                value={comment}
                                                                                onChange={handleInputChange}
                                                                                placeholder="댓글을 입력해주세요."></textarea>
                                                                                <div class='float-end'>
                                                                                    <button type="button"
                                                                                            className="btn btn-success"
                                                                                            type='submit'>댓글 등록
                                                                                    </button>
                                                                                </div>
                                                                            </form>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </p>
                                                        </div>
                                                        <p><a href={book.bookLink} target="_blank" rel="noopener noreferrer">자세히 보기</a></p>
                                                    </div>
                                                </td>
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
        </section>
    );
}

export default Search;
