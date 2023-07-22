<!--
  * Copyright 2023; Réal Demers.
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
  -->
<template>
    <div>
        <ul class="vrd_navigation">

            <!-- Navigate through the pages. -->
            <li class="vrd_navigation_item_left">
                <button class="vrd_item" id="prev_page" @click=prevPage()>
                    <font-awesome-icon class="fa-lg" icon="fa-solid fa-arrow-left"/>
                </button>
                <input class="vrd_item vrd_item_width" type="number" id="current_page" v-model="noPage"/>
                <button class="vrd_item" id="next_page" @click=nextPage()>
                    <font-awesome-icon class="fa-lg" icon="fa-solid fa-arrow-right"/>
                </button>
            </li>

            <!-- Information on the current page. -->
            <li class="vrd_navigation_item_left">
                <span class="vrd_item" id="page_num">{{ docSummary }}</span>
            </li>

            <!-- Zoom In / Out. -->
            <li class="vrd_navigation_item_right">
                <button class="vrd_item" id="printing" @click=print()>
                    <font-awesome-icon class="fa-lg" icon="fa-solid fa-print"/>
                </button>
                <button class="vrd_item" id="zoom_in" @click=scaleIN()>
                    <font-awesome-icon class="fa-lg" icon="fa-solid fa-magnifying-glass-plus"/>
                </button>
                <button class="vrd_item" id="zoom_out" @click=scaleOUT()>
                    <font-awesome-icon class="fa-lg" icon="fa-solid fa-magnifying-glass-minus"/>
                </button>
            </li>
        </ul>

        <!-- Container for the PDF. -->
        <div class="vrd_viewport">
            <canvas id="canvas" ref="canvasReference" class="vrd_canvas"/>
        </div>
    </div>
</template>

<script setup lang="ts">
    import { onMounted, ref, watch } from "vue";

    import { PDFService }          from "@/services/PDFService";
    import { PDFServiceException } from "@/services/PDFServiceException";
    import { PDFServiceCode }      from "@/services/PDFServiceCode";

    name: "vrd-pdfviewer";
    const props = defineProps({ url: String });

    const { t } = useI18n();
    const noPage = ref<number>(0);
    const pdfService:PDFService = new PDFService(props.url as string);
    const canvasReference = ref<HTMLCanvasElement | null>(null); // Get access to canvas.

    const docSummary = computed<string>(() => {
        return t('vrd-pdfviewer.page') + noPage.value + t('vrd-pdfviewer.of') + pdfService.getNumPages();
    })

    onMounted((): void => {
        pdfService.init(canvasReference.value as HTMLCanvasElement).then(() => {
            pdfService.render().then(() => {
                noPage.value = pdfService.getPage();
            }).catch((e: Error) => {
                if (e instanceof PDFServiceException) {
                    switch((e as PDFServiceException).getCode()) { 
                        case PDFServiceCode.NO_SOURCE: { 
                            displayMessage(t('vrd-pdfviewer.ex_no_source')); 
                            break; 
                        } 
                        case PDFServiceCode.NO_NODE_SUPPORTED: { 
                            displayMessage(t('vrd-pdfviewer.ex_no_node_supported')); 
                            break; 
                        } 
                        default: { 
                            displayMessage(t('vrd-pdfviewer.ex_unknown')); 
                            break; 
                        } 
                    } 
                }
                console.log(e);
            })          
        })
    })    

    function displayMessage(msg:string) : void {
        if (!msg) {
            return;
        }

        const context:CanvasRenderingContext2D = (canvasReference.value  as HTMLCanvasElement).getContext('2d') as CanvasRenderingContext2D;
        context.font = "20px Arial";
        context.fillStyle = "rgb(139,0,0)"; // Dark red.
        context.textAlign = "center";

        const width:number = (canvasReference.value  as HTMLCanvasElement).width/2; 
        const height:number = (canvasReference.value  as HTMLCanvasElement).height/2;

        context.fillText(msg, width, height);
    }
            
    function prevPage(): void {
        pdfService.prevPage();
        noPage.value = pdfService.getPage(); 
    }

    function nextPage(): void {
        pdfService.nextPage();
        noPage.value = pdfService.getPage(); 
    }

    function print(): void {
        pdfService.print();
    }

    function scaleIN(): void {
        pdfService.scaleIN();
    }

    function scaleOUT(): void {
        pdfService.scaleOUT();
    }

    watch(noPage, function(newValue, oldValue) {
        pdfService.setPage(newValue);
        if (pdfService.getPage() != newValue)
            noPage.value = pdfService.getPage();
    })
 </script>

<style scoped>
    .vrd_navigation {
        list-style-type: none;
        margin: 0;
        padding: 0;
        overflow: hidden;
        background-color: #333333;
    }
    .vrd_navigation_item_left {
        float: left;
    }
    .vrd_navigation_item_right {
        float: right;
    }
    .vrd_item {
        float: left;
        display: block;
        color: white;
        text-align: center;
        padding: 16px;
        text-decoration: none;
    }
    .vrd_item_width {
        width: 100px;
    }
    .vrd_item:hover {
        background-color: #111111;
    }
    .vrd_viewport{
        background-color: rgb(226, 226, 226);
        overflow-y: scroll;
        width: auto;
        height: 400px;
    }
    .vrd_canvas {
        padding: 0;
        margin-left: auto;
        margin-right: auto;
        display: block;
    }
</style>