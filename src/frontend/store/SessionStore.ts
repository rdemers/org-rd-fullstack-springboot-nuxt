import { defineStore } from "pinia";
import Session from "@/types/Session";

export const SessionStore = defineStore({
    id: "SessionStore",

    state: () => ({
        currentSession: {
            jwtToken: "",
            user: "",
            roles: [""]
        } as Session,
    }),

    getters: {
        getSession():Session {
            return this.currentSession;
        }
    },

    actions: {
        setSession(session: Session) {
            this.currentSession = session;
        },

        clearSession() {
            this.currentSession = {
                jwtToken: "",
                user: "",
                roles: [""]
            }
        },
 
        isActiveSession(): boolean {
            if (this.currentSession.jwtToken.trim().length !== 0) {
                return true;
            }
            return false;
        }
    }
})
