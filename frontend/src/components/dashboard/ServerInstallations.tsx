import {useEffect, useState} from "react";
import {Grid, SelectChangeEvent} from "@mui/material";
import {useInterval} from "../../hooks/use-interval";
import {changeServerBranch, getServerInstallations, installServer} from "../../services/serverInstallationsService";
import ServerInstallationItem from "./ServerInstallationItem";
import {ServerInstallationDto} from "../../dtos/ServerInstallationDto.ts";
import {ServerType} from "../../dtos/ServerDto.ts";

const ServerInstallations = () => {
    const [serverInstallations, setServerInstallations] = useState<Array<ServerInstallationDto>>([]);

    useEffect(() => {
        void fetchServerInstallations();
    }, []);

    useInterval(() => fetchServerInstallations(), 5000);

    const fetchServerInstallations = async () => {
        const {data: serverInstallationsDto} = await getServerInstallations();
        const installations = serverInstallationsDto.serverInstallations
            .sort((a: ServerInstallationDto, b: ServerInstallationDto) => a.type.localeCompare(b.type));
        setServerInstallations(installations);
    };

    const handleUpdateClicked = async (serverType: ServerType) => {
        await installServer(serverType);

        setServerInstallations(prevState => {
            const newState = [...prevState];
            const installation = newState.find(i => i.type === serverType);
            if (!installation) {
                return prevState;
            }

            installation.installationStatus = "INSTALLATION_IN_PROGRESS";
            installation.errorStatus = null;
            return newState;
        });
    };

    const handleBranchChanged = async (e: SelectChangeEvent, serverType: ServerType) => {
        const selectedBranch = e.target.value;

        setServerInstallations(prevState => {
            const newState = [...prevState];
            const installation = newState.find(i => i.type === serverType);
            if (!installation) {
                return prevState;
            }

            installation.branch = selectedBranch;
            return newState;
        });

        await changeServerBranch(serverType, selectedBranch);
    };

    return (
        <>
            <Grid container spacing={2}>
                {serverInstallations.map(installation => (
                    <Grid item xs={12} md={6} key={installation.type}>
                        <ServerInstallationItem installation={installation}
                                                onBranchChanged={handleBranchChanged}
                                                onUpdateClicked={handleUpdateClicked}
                        />
                    </Grid>
                ))}
            </Grid>
        </>
    );
};

export default ServerInstallations;