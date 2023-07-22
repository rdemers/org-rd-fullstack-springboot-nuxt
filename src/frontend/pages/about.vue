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
            <blockquote class="blockquote">
                {{$t('about.licence01')}}<br>
                {{$t('about.licence02')}}<br>
                {{$t('about.licence03')}}<br>
                <br>
                {{$t('about.licence04')}}<br>
                <br>
                {{$t('about.licence05')}}<br>
                <br>
                {{$t('about.licence06')}}<br>
                {{$t('about.licence07')}}<br>
                {{$t('about.licence08')}}<br>
                <br>
                <a href="http://www.apache.org/licenses/LICENSE-2.0" 
                   target="_blank" rel="noopener noreferrer">{{$t('about.licence09')}}</a>
                <br>
                <br>
                {{$t('about.licence10')}}<br>
                {{$t('about.licence11')}}<br>
                {{$t('about.licence12')}}<br>
                {{$t('about.licence13')}}<br>
                {{$t('about.licence14')}}
            </blockquote>
        </v-col>
    </v-row>
</template>

<script setup lang="ts">
    name: "about";
</script>

<style scoped>
</style>
