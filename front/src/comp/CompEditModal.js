import {
    axiosInstance, CONFIG,
    isLoginedSelector,
    loginedUserInfoSelector, queryClient, Toast,
} from "../recoil";
import {useRecoilValue, useSetRecoilState} from "recoil";
import CompComment from "./CompComment";
import Swal from "sweetalert2";
import {useEffect, useRef, useState} from "react";
import {useLocation} from "react-router-dom";

function CompEditModal({book}) {
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const searchText = queryParams.get("searchText") || "";
    const page = parseInt(queryParams.get("page") || "1") - 1;
    const closeButtonRef = useRef(null);
    const [bookName, setBookName] = useState(book.bookName);
    const [bookAuthor, setBookAuthor] = useState(book.bookAuthor);
    const [bookPublisher, setBookPublisher] = useState(book.bookPublisher);
    const [bookDescription, setBookDescription] = useState(book.bookDescription);
    const [bookImg, setBookImg] = useState(book.bookImageUrl);
    const [previewBookImg, setPreviewBookImg] = useState('');
    const [selectedFile, setSelectedFile] = useState(null);
    const inputRef = useRef(null);
    const encodeFileToBase64 = (fileBlob) => {
        const reader = new FileReader();
        reader.readAsDataURL(fileBlob);
        return new Promise((resolve) => {
            reader.onload = () => {
                setPreviewBookImg(reader.result);
                resolve();
            };
        });
    };
    const bookNameInputChange = (event) => {
        setBookName(event.target.value);
    };
    const bookAuthorInputChange = (event) => {
        setBookAuthor(event.target.value);
    };
    const bookPublisherInputChange = (event) => {
        setBookPublisher(event.target.value);
    };
    const bookDescriptionInputChange = (event) => {
        setBookDescription(event.target.value);
    };

    const handleFileInputChange = (event) => {
        const file = event.target.files[0];
        encodeFileToBase64(event.target.files[0]);
        setBookImg('')
        setSelectedFile(file);
    }
    const edit = async (event, bookNumber) => {
        event.preventDefault();

        // FormData 객체 생성
        const formData = new FormData();
        formData.append('bookEditDto', new Blob([JSON.stringify({
            bookNumber: bookNumber,
            bookName: bookName,
            bookAuthor: bookAuthor,
            bookPublisher: bookPublisher,
            bookDescription: bookDescription,
            bookImageUrl : bookImg
        })], { type: 'application/json' }));
        formData.append('bookImg', selectedFile);
        const config = {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        };
        try {
            const response = await axiosInstance.post(CONFIG.API_EDIT_BOOK, formData);

            Toast.fire({
                icon: 'success',
                title: response.data.message
            });

            setBookName(bookName);
            setBookAuthor(bookAuthor);
            setBookPublisher(bookPublisher);
            setBookDescription(bookDescription);
            if (inputRef.current) {
                inputRef.current.value = null;
            }
            setPreviewBookImg('');
            setBookImg(response.data.bookImg)

            queryClient.invalidateQueries(["bookList", page, searchText]);
            closeButtonRef.current.click();
        } catch (e) {
            console.log(e)
        }
    }
    const searchBook = async (bookName) => {
        try{
            const response = await axiosInstance.get(`${CONFIG.API_EDIT_SEARCH_BOOK}${bookName}`);
            setBookName(response.data.bookName)
            setBookAuthor(response.data.bookAuthor);
            setBookPublisher(response.data.bookPublisher);
            setBookDescription(response.data.bookDescription);
            setBookImg(response.data.bookImageUrl)
        } catch (e) {
            console.log(e)
        }
    }

    useEffect(() => {
        const modalElement = document.getElementById(`editModal${book.bookNumber}`);
        const bookNameInputElement = document.getElementById(`bookNameInput${book.bookNumber}`);

        modalElement.addEventListener('shown.bs.modal', function () {
            bookNameInputElement.focus();
        });

        return () => {
            modalElement.removeEventListener('shown.bs.modal', function () {
                bookNameInputElement.focus();
            });
        }
    }, [book.bookNumber]);
    return (
        <div>
            <div style={{display : 'flex', alignItems : 'center'}} type="button" className="btn btn-outline-success btn-sm" data-bs-toggle="modal"
                 data-bs-target={`#editModal${book.bookNumber}`}>
                <div>책 수정</div>
            </div>
        <div className="modal fade" id={`editModal${book.bookNumber}`}
             tabIndex="-1"
             aria-labelledby={`editModal${book.bookNumber}Label`}
             aria-hidden="true">
            <div className="modal-dialog modal-dialog-centered modal-xl">
            <div className="modal-content">
                <div className="modal-header">
                    <h6 className="modal-title"
                        id={`editModal${book.bookNumber}Label`}>책 수정</h6>
                    <button type="button"
                            className="btn-close"
                            data-bs-dismiss="modal"
                            ref={closeButtonRef}
                            aria-label="Close"></button>
                </div>
                <div className="modal-body">
                    <section>
                        <form onSubmit={(event) => edit(event, book.bookNumber)}>
                            <div class="card bg-light">
                                <div class="card-body flex-center">
                                    <div style={{padding: "5% 0 5% 0"}}>
                                        {bookImg && <img src={bookImg} alt="Book Cover" style={{maxWidth : '80%'}} />}
                                        {previewBookImg && <img src={previewBookImg} alt="preview-img" style={{maxWidth : '80%'}}/>}
                                        <div className="margin-top30">
                                            <label className="form-label">책 표지</label>
                                            <input className="form-control" type="file" name="bookImg" ref={inputRef} onChange={handleFileInputChange} style={{maxWidth : '80%'}}/>
                                        </div>
                                    </div>
                                    <div className="margin-left8">
                                        <label className="form-label">제목</label>
                                        <input className="form-control me-2 mb-3" name="bookName" id={`bookNameInput${book.bookNumber}`} value={bookName} onChange={bookNameInputChange}/>
                                        <div>
                                            <button className="btn btn-outline-danger btn-sm mb-3" type="button" onClick={() => searchBook(bookName)}>제목으로 검색</button>
                                        </div>
                                        <label className="form-label">저자</label>
                                        <input className="form-control me-2 mb-3" name="bookAuthor" value={bookAuthor} onChange={bookAuthorInputChange}/>
                                        <label className="form-label">출판사</label>
                                        <input className="form-control me-2 mb-3" name="bookPublisher" value={bookPublisher} onChange={bookPublisherInputChange}/>
                                        <label className="form-label">소개</label>
                                        <textarea className="form-control me-2 mb-3 modal-textarea" name="bookDescription" value={bookDescription} onChange={bookDescriptionInputChange}/>
                                    </div>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button"
                                        className="btn btn-success"
                                        type='submit'>수정
                                </button>
                            </div>
                        </form>
                    </section>
                </div>
            </div>
            </div>
            </div>
        </div>
    );
}

export default CompEditModal;
