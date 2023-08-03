import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import {RecoilRoot} from "recoil";
import {QueryClientProvider} from "react-query";
import {queryClient} from "./recoil";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <RecoilRoot>
        <QueryClientProvider client={queryClient}>
          <React.StrictMode>
            <App />
          </React.StrictMode>
        </QueryClientProvider>
    </RecoilRoot>
);

reportWebVitals();
