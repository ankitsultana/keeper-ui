
import React, { useState, useEffect } from 'react';
import { Sidebar, SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { ZookeeperSidebar } from "@/components/ZookeeperSidebar";
import { ZNodeContent } from "@/components/ZNodeContent";
import { Separator } from "@/components/ui/separator";

export interface ZNode {
  path: string;
  data: string;
  children: string[];
  stat: {
    ctime: number;
    mtime: number;
    version: number;
    cversion: number;
    aversion: number;
    ephemeralOwner: number;
    dataLength: number;
    numChildren: number;
  };
}

const ZookeeperBrowser = () => {
  const [selectedNode, setSelectedNode] = useState<string>('/');
  const [nodeData, setNodeData] = useState<ZNode | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchNodeData = async (path: string) => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:12345/get?path=${encodeURIComponent(path)}`);
      if (!response.ok) {
        throw new Error(`Failed to fetch node data: ${response.statusText}`);
      }
      const data = await response.json();
      setNodeData(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error occurred');
      console.error('Error fetching node data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNodeData(selectedNode);
  }, [selectedNode]);

  const handleNodeSelect = (path: string) => {
    setSelectedNode(path);
  };

  const handleRefresh = () => {
    fetchNodeData(selectedNode);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50">
      <SidebarProvider>
        <div className="flex w-full min-h-screen">
          <ZookeeperSidebar 
            selectedNode={selectedNode}
            onNodeSelect={handleNodeSelect}
            onRefresh={handleRefresh}
          />
          <main className="flex-1 flex flex-col">
            <header className="bg-white border-b border-slate-200 p-4 shadow-sm">
              <div className="flex items-center gap-4">
                <SidebarTrigger className="hover:bg-slate-100 p-2 rounded-md transition-colors" />
                <Separator orientation="vertical" className="h-6" />
                <div className="flex items-center gap-2">
                  <h1 className="text-2xl font-bold text-slate-800">Zookeeper Browser</h1>
                  <div className="text-sm text-slate-500 bg-slate-100 px-2 py-1 rounded-md">
                    localhost:12345
                  </div>
                </div>
              </div>
            </header>
            <div className="flex-1 p-6">
              <ZNodeContent 
                nodeData={nodeData}
                selectedPath={selectedNode}
                loading={loading}
                error={error}
                onRefresh={handleRefresh}
              />
            </div>
          </main>
        </div>
      </SidebarProvider>
    </div>
  );
};

export default ZookeeperBrowser;
