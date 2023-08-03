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
                <Link to="/" class="navbar-brand">{props.nav.title}</Link>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation"><span class="navbar-toggler-icon"></span></button>
                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
                        {menuList}
                        {isLogined && (
                            <li className="nav-item dropdown">
                                <a className="nav-link dropdown-toggle" id="navbarDropdownBlog" href="#" role="button"
                                   data-bs-toggle="dropdown" aria-expanded="false">마이 페이지</a>
                                <ul className="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdownBlog">
                                    <li><a className="dropdown-item">내 정보</a></li>
                                    <li><a className="dropdown-item">비밀번호 변경</a></li>
                                    <li><a className="dropdown-item">비밀번호 변경</a></li>
                                </ul>
                            </li>
                        )}
                        {isLogined && (
                            <li className="nav-item"><Link to={"/logout"} class="nav-link">로그아웃</Link></li>
                        )}
                        {isLogined && (
                            <li className="nav-item"><Link to={"/"} class="nav-link">{loginedUserInfo.name}</Link></li>
                        )}
                        {isLogined || (
                            <li className="nav-item"><Link to={"/login"} class="nav-link">로그인</Link></li>
                        )}
                        {isAdmin && (
                            <li className="nav-item dropdown">
                                <a className="nav-link dropdown-toggle" id="navbarDropdownBlog" href="#" role="button"
                                   data-bs-toggle="dropdown" aria-expanded="false">관리자 페이지</a>
                                <ul className="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdownBlog">
                                    <li><Link to={"/add"} className="dropdown-item">책 추가</Link></li>
                                    <li><a className="dropdown-item">Tempate1</a></li>
                                    <li><a className="dropdown-item">Template2</a></li>
                                </ul>
                            </li>
                        )}
                    </ul>
                </div>
            </div>
        </nav>
    );
}

export default NavBar;
