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
  -->
<template>
    <v-row>
        <v-col class="text-center">
            <vrd-osl-logo/>
            <blockquote class="blockquote">{{$t('ui.title')}}</blockquote>
        </v-col>
    </v-row>
    <v-row>
        <v-divider :thickness="4" class="border-opacity-100" color="success"/>
        <v-expansion-panels>
            <v-expansion-panel :title="$t('ui.icon_title')" :text="$t('ui.icon_text')">
                <v-col>
                    <font-awesome-icon class="fa-3x" icon="fa-brands fa-twitter-square"/>
                    <font-awesome-icon class="fa-3x" icon="fa-brands fa-youtube-square"/>
                    <font-awesome-icon class="fa-3x" icon="fa-brands fa-github-square"/>
                    <font-awesome-icon class="fa-2x" icon="fa-solid fa-plus"/>
                    <font-awesome-icon class="fa-2x" icon="fa-solid fa-minus"/>
                    <font-awesome-icon class="fa-2x" icon="fa-regular fa-circle-left"/>
                    <font-awesome-icon class="fa-2x" icon="fa-regular fa-circle-right"/>
                    <font-awesome-icon class="fa-2x" icon="fa-solid fa-arrow-right"/>
                    <font-awesome-icon class="fa-2x" icon="fa-solid fa-arrow-left"/>
                    <font-awesome-icon class="fa-2x" icon="fa-solid fa-magnifying-glass-plus"/>
                    <font-awesome-icon class="fa-2x" icon="fa-solid fa-magnifying-glass-minus"/>
                </v-col>
            </v-expansion-panel>
        </v-expansion-panels>
    </v-row>
    <v-row>
        <v-divider :thickness="4" class="border-opacity-100" color="success"/>
        <v-expansion-panels>
            <v-expansion-panel :title="$t('ui.slot_title')" :text="$t('ui.slot_text')">
                <div class="text-center">
                    <v-menu offset-y>
                        <template v-slot:activator="{props}">
                            <v-btn flat v-bind="props">
                                <v-icon left>mdi-translate-variant</v-icon>
                                <span class="mx-4">Français</span>
                            </v-btn>
                        </template>
                        <v-list>
                            <v-list-item v-for="lang in langs" :key="lang.iso">
                                <v-list-item-title>{{lang.description}} - ({{lang.iso}})</v-list-item-title>
                            </v-list-item>
                        </v-list>
                    </v-menu>
                </div> 
            </v-expansion-panel>
        </v-expansion-panels>
    </v-row>
    <v-row>
        <v-divider :thickness="4" class="border-opacity-100" color="success"/>
        <v-expansion-panels>
            <v-expansion-panel :title="$t('ui.pdf_title')" :text="$t('ui.pdf_text')">
                <div>
                    <vrd-pdfviewer url=""/> <!-- No rendering to do. Just a demo. -->
                </div>
              </v-expansion-panel>
        </v-expansion-panels>
    </v-row>
</template>

<script lang="ts">
    export default defineComponent({
        name: "ui",

        setup() {
            const langs = [ 
                { iso: "fr-CA", description: "Français" },
                { iso: "en-CA", description: "English" }
            ];
            return {
                langs
            }
        }
    });
</script>

<style scoped>
</style>