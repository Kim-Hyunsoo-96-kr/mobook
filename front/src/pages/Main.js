import '../App.css';
import Header from "../comp/Header";
import NavBar from "../comp/NavBar";
import Section1 from "../comp/Section1";
import Section2 from "../comp/Section2";
import {BrowserRouter, Route, Routes} from "react-router-dom";

function Main() {
  return (
          <div>
            <Header/>
            <Section1/>
            <Section2/>
          </div>
  );
}

export default Main;
