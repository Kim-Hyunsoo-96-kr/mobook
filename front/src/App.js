import './App.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Main from "./pages/Main";
import NavBar from "./comp/NavBar";
import Login from "./pages/Login";
import axios from "axios";
import Logout from "./pages/Logout";
import Test from "./pages/Test";
import Search from "./pages/Search";
import AddBook from "./pages/AddBook";
function App() {
    const nav = {
        title: "MOBOOK1.0",
        menu: [
            {title: "홈", router: "/"},
            {title: "책 검색", router: "/search"},
            {title: "책 대여", router: "/rent"},
            {title: "책 반납", router: "/return"},
            {title: "책 요청", router: "/request"},
        ]
    }
  return (
      <BrowserRouter>
          <div>
          <NavBar nav={nav}/>
          <Routes>
              <Route path="/" element={<Main />} />
              <Route path="/login" element={<Login />} />
              <Route path="/logout" element={<Logout />} />
              <Route path="/search" element={<Search />} />
              <Route path="/add" element={<AddBook />} />
              <Route path="/test" element={<Test />} />
          </Routes>
          </div>
      </BrowserRouter>
  );
}

export default App;
