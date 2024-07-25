/*
 * Copyright 2023, 2024; Réal Demers.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
import type Session                   from "@/types/Session";
import { type PDFDocumentLoadingTask, 
         type PDFDocumentProxy, 
         type PDFPageProxy, 
         type PageViewport }          from "pdfjs-dist";

import { SessionStore }               from "@/store/SessionStore";
import { PDFServiceCode }             from "@/services/PDFServiceCode";
import { PDFServiceException }        from "@/services/PDFServiceException";

import { fromByteArray }              from "base64-js";
import printJS                        from "print-js";
import * as pdfjs                     from 'pdfjs-dist';

// Webpack allows to initialize correctly.
// pdfjs.GlobalWorkerOptions.workerSrc = pdfjsWorker.
import "pdfjs-dist/webpack"; 

// Old code.
//if (typeof window !== "undefined" && "Worker" in window) {
//    pdfjs.GlobalWorkerOptions.workerPort = new Worker(
//      new URL("./build/pdf.worker.js", import.meta.url)
//    );
//  }
//const pdfjs = require("pdfjs-dist/webpack");

export class PDFService {

    private pdfSource:string;
    private numPages:number;
    private currentPage:number;
	private scale:number;

    private canvas?:HTMLCanvasElement;
	private documentProxy?:PDFDocumentProxy;

    constructor(pdfSource:string) {

        this.pdfSource   = pdfSource;
        this.numPages    = 0;
        this.currentPage = 1;
        this.scale       = 1.0;

        this.canvas        = undefined;
        this.documentProxy = undefined;
    }

    public async init(canvas:HTMLCanvasElement) : Promise<void> {

        // Know our canvas.
        this.canvas = canvas;

        if (pdfjs === undefined) {
            console.log("pdfjs is not defined. PDF.JS is not very functional on Node.");
            return;
        }

        if (! this.pdfSource)
        {
            console.log("No source defined... Demo mode?");
            return;
        }

        // Obtain the document in PDF format.
        // The HTTP request is secure. We need to pass our session token.
        const session:Session = SessionStore().getSession;
        const loadingTask:PDFDocumentLoadingTask = pdfjs.getDocument({
            url:this.pdfSource,
            httpHeaders: {
                Authorization: session.jwtToken,
                Accept: "application/pdf",
            }
        });

        // Complete initialization.
        this.documentProxy = await loadingTask.promise;
        this.numPages      = this.documentProxy.numPages;
    }

    public async render() : Promise<void> {

        if (! this.pdfSource) {
            throw new PDFServiceException(PDFServiceCode.NO_SOURCE);
        }
        
        if (this.documentProxy === undefined) {
            throw new PDFServiceException(PDFServiceCode.NO_NODE_SUPPORTED);
        }

        // Scale adjustment.
	    const pageProxy:PDFPageProxy = await (this.documentProxy as PDFDocumentProxy).getPage(this.currentPage);
        const viewport:PageViewport = pageProxy.getViewport({scale: this.scale});

        // Apply the dimensions to our <canvas> element.
	    const context:CanvasRenderingContext2D = (this.canvas as HTMLCanvasElement).getContext('2d') as CanvasRenderingContext2D;
	    (this.canvas as HTMLCanvasElement).height = viewport.height;
	    (this.canvas as HTMLCanvasElement).width = viewport.width;

	    // Render to the canvas.
	    pageProxy.render({canvasContext: context, viewport: viewport});
    }

    public prevPage() : void {

        if (this.documentProxy === undefined || this.currentPage <= 1) {
            return;
        }
        this.currentPage--;
        this.render();
    }
    
    public nextPage() : void {

        if (this.documentProxy === undefined || this.currentPage >= this.numPages) {
            return;
        }
        this.currentPage++;
        this.render();
    }

    public getNumPages() : number {
        return this.numPages;
    }
    
    public getPage() : number {
        return this.currentPage;
    }

    public setPage(noPage:number) : void {

        if (this.documentProxy === undefined || noPage < 1 ||
            noPage > this.numPages           || this.currentPage == noPage) {
            return;
        }
        this.currentPage = noPage;
        this.render();
        return;
    }

    public print() : void {

        if (this.documentProxy === undefined) {
            return;
        }

        // The next line allows only the current page.
        // printJS((this._canvas as HTMLCanvasElement).id,"html");

        // The whole document.
        (this.documentProxy as PDFDocumentProxy).getData().then((arrayBuffer: Uint8Array) => {
            const pdfBase64Source:string = fromByteArray(arrayBuffer);
            if (typeof window !== "undefined") {
               printJS({printable:pdfBase64Source, type: "pdf", base64: true})
            }
        });
    }

    public scaleIN() : void {

        if (this.documentProxy === undefined) {
            return;
        }   
        this.scale *= 4 / 3;
        this.render();
    }

    public scaleOUT() : void {

        if (this.documentProxy === undefined) {
            return;
        }      
        this.scale *= 2 / 3;
        this.render();
    }
}