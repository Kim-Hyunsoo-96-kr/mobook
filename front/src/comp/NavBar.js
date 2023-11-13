import {Link, Navigate} from "react-router-dom";
import {useRecoilValue} from "recoil";
import {isLoginedSelector, loginedUserInfoSelector} from "../recoil";

function NavBar(props) {
    const menuList = []
    for(let i = 0; i < props.nav.menu.length; i ++){
        let t = props.nav.menu[i];
        menuList.push(<li class="nav-item" key={i}><Link to={t.router} class="nav-link">{t.title}</Link></li>);
    }
    const isLogined = useRecoilValue(isLoginedSelector);
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    let isAdmin = null
    if(isLogined) isAdmin = loginedUserInfo.isAdmin
    return (
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
            <div class="container px-5">
                <Link to="/" className="navbar-brand">
                    <img src="https://raw.githubusercontent.com/jootang2/MyS3/8ecc030e23d953296f92545f8f9e4dbb61fb7ad7/MOBOOK1.2/MOBOOK%201.2%20logo.png" alt="Logo" />
                </Link>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation"><span class="navbar-toggler-icon"></span></button>
                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
                        {menuList}
                        {isLogined && (
                            <li className="nav-item dropdown">
                                    <a className="nav-link dropdown-toggle" id="navbarDropdownBlog" href="#" role="button"
                                       data-bs-toggle="dropdown" aria-expanded="false">내 책 관리</a>
                                <ul className="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdownBlog">
                                    <li><Link to="/myBookLog?searchText=&page=1" className="dropdown-item">내 기록</Link></li>
                                    <li><Link to="/myRentBook" className="dropdown-item">대여 내역</Link></li>
                                    <li><Link to="/myRecommendBook?searchText=&page=1" className="dropdown-item">찜한 책 내역</Link></li>
                                    <li><Link to="/myRequestBook?searchText=&page=1" className="dropdown-item">요청한 책 내역</Link></li>
                                </ul>
                            </li>
                        )}
                        {isLogined && (
                            <li className="nav-item dropdown">
                                    <a className="nav-link dropdown-toggle" id="navbarDropdownBlog" href="#" role="button"
                                       data-bs-toggle="dropdown" aria-expanded="false">{loginedUserInfo.name} 님</a>
                                <ul className="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdownBlog">
                                    <li><Link to="/passwordChange" className="dropdown-item">비밀번호 변경</Link></li>
                                    <li><Link to={"/logout"} class="dropdown-item">로그아웃</Link></li>
                                </ul>
                            </li>
                        )}
                        {isAdmin && (
                            <li className="nav-item dropdown">
                                <a className="nav-link dropdown-toggle" id="navbarDropdownBlog" href="#" role="button"
                                   data-bs-toggle="dropdown" aria-expanded="false">Admin</a>
                                <ul className="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdownBlog">
                                    <li><Link to={"/createAccount"} className="dropdown-item">계정 생성</Link></li>
                                    <li><Link to={"/add"} className="dropdown-item">책 추가</Link></li>
                                    <li><Link to={"/adminBookLog?searchText=&page=1"} className="dropdown-item">전체 대여/반납 기록 보기</Link></li>
                                    <li><Link to={"/adminRentBook?searchText=&page=1"} className="dropdown-item">현재 대여 중 기록 보기</Link></li>
                                    <li><Link to={"/adminRequestBook?searchText=&page=1"} className="dropdown-item">책 신청 기록 보기</Link></li>
                                </ul>
                            </li>
                        )}
                        {isLogined || (
                            <li className="nav-item"><Link to={"/login"} class="nav-link">로그인</Link></li>
                        )}

                    </ul>
                </div>
            </div>
        </nav>
    );
}

export default NavBar;
