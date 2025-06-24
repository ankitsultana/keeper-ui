
import React, { useState, useEffect } from 'react';
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarHeader,
} from "@/components/ui/sidebar";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ChevronRight, ChevronDown, Folder, FolderOpen, Plus, RefreshCw, Database } from "lucide-react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { useToast } from "@/hooks/use-toast";

interface TreeNode {
  path: string;
  children: TreeNode[];
  expanded: boolean;
  hasChildren: boolean;
}

interface ZookeeperSidebarProps {
  selectedNode: string;
  onNodeSelect: (path: string) => void;
  onRefresh: () => void;
}

export const ZookeeperSidebar: React.FC<ZookeeperSidebarProps> = ({
  selectedNode,
  onNodeSelect,
  onRefresh
}) => {
  const [tree, setTree] = useState<TreeNode>({
    path: '/',
    children: [],
    expanded: true,
    hasChildren: true
  });
  const [newNodeName, setNewNodeName] = useState('');
  const [newNodeData, setNewNodeData] = useState('');
  const [selectedParent, setSelectedParent] = useState('/');
  const [dialogOpen, setDialogOpen] = useState(false);
  const { toast } = useToast();

  const fetchChildren = async (path: string): Promise<string[]> => {
    try {
      const response = await fetch(`http://localhost:12345/ls?path=${encodeURIComponent(path)}`);
      if (!response.ok) {
        throw new Error(`Failed to fetch children: ${response.statusText}`);
      }
      const data = await response.json();
      return data.children || [];
    } catch (error) {
      console.error('Error fetching children:', error);
      return [];
    }
  };

  const loadTreeNode = async (node: TreeNode): Promise<TreeNode> => {
    const children = await fetchChildren(node.path);
    const childNodes: TreeNode[] = children.map(child => ({
      path: node.path === '/' ? `/${child}` : `${node.path}/${child}`,
      children: [],
      expanded: false,
      hasChildren: true // We'll assume nodes might have children
    }));

    return {
      ...node,
      children: childNodes,
      hasChildren: children.length > 0
    };
  };

  const toggleNode = async (path: string) => {
    const updateTree = async (node: TreeNode): Promise<TreeNode> => {
      if (node.path === path) {
        if (!node.expanded && node.children.length === 0) {
          const updatedNode = await loadTreeNode(node);
          return { ...updatedNode, expanded: true };
        }
        return { ...node, expanded: !node.expanded };
      }

      const updatedChildren = await Promise.all(
        node.children.map(child => updateTree(child))
      );
      
      return { ...node, children: updatedChildren };
    };

    const newTree = await updateTree(tree);
    setTree(newTree);
  };

  const createNode = async () => {
    if (!newNodeName.trim()) {
      toast({
        title: "Error",
        description: "Node name cannot be empty",
        variant: "destructive",
      });
      return;
    }

    const fullPath = selectedParent === '/' ? `/${newNodeName}` : `${selectedParent}/${newNodeName}`;
    
    try {
      const response = await fetch('http://localhost:12345/create', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          path: fullPath,
          data: newNodeData,
        }),
      });

      if (!response.ok) {
        throw new Error(`Failed to create node: ${response.statusText}`);
      }

      toast({
        title: "Success",
        description: `Node ${fullPath} created successfully`,
      });

      setNewNodeName('');
      setNewNodeData('');
      setDialogOpen(false);
      
      // Refresh the tree
      const newTree = await loadTreeNode(tree);
      setTree(newTree);
      onRefresh();
    } catch (error) {
      toast({
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to create node",
        variant: "destructive",
      });
    }
  };

  useEffect(() => {
    loadTreeNode(tree).then(setTree);
  }, []);

  const renderTree = (node: TreeNode, level: number = 0) => {
    const isSelected = node.path === selectedNode;
    const Icon = node.expanded ? FolderOpen : Folder;
    const ChevronIcon = node.expanded ? ChevronDown : ChevronRight;

    return (
      <div key={node.path}>
        <SidebarMenuItem>
          <SidebarMenuButton
            onClick={() => {
              onNodeSelect(node.path);
              if (node.hasChildren) {
                toggleNode(node.path);
              }
            }}
            className={`w-full justify-start gap-2 ${isSelected ? 'bg-blue-100 text-blue-900' : 'hover:bg-slate-100'} transition-colors`}
            style={{ paddingLeft: `${level * 12 + 8}px` }}
          >
            <div className="flex items-center gap-1 min-w-0 flex-1">
              {node.hasChildren && (
                <ChevronIcon className="h-4 w-4 shrink-0" />
              )}
              <Icon className="h-4 w-4 shrink-0 text-blue-600" />
              <span className="truncate text-sm">{node.path === '/' ? 'root' : node.path.split('/').pop()}</span>
            </div>
            <Button
              size="sm"
              variant="ghost"
              className="h-6 w-6 p-0 opacity-0 group-hover:opacity-100 transition-opacity"
              onClick={(e) => {
                e.stopPropagation();
                setSelectedParent(node.path);
                setDialogOpen(true);
              }}
            >
              <Plus className="h-3 w-3" />
            </Button>
          </SidebarMenuButton>
        </SidebarMenuItem>
        {node.expanded && node.children.map(child => renderTree(child, level + 1))}
      </div>
    );
  };

  return (
    <Sidebar className="border-r bg-white/50 backdrop-blur-sm">
      <SidebarHeader className="border-b bg-white/80 p-4">
        <div className="flex items-center gap-2">
          <Database className="h-5 w-5 text-blue-600" />
          <span className="font-semibold text-slate-800">ZK Tree</span>
          <Button
            size="sm"
            variant="ghost"
            onClick={() => loadTreeNode(tree).then(setTree)}
            className="ml-auto h-8 w-8 p-0"
          >
            <RefreshCw className="h-4 w-4" />
          </Button>
        </div>
      </SidebarHeader>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupContent>
            <SidebarMenu className="group">
              {renderTree(tree)}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Create New ZNode</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div>
              <Label htmlFor="parent">Parent Path</Label>
              <Input
                id="parent"
                value={selectedParent}
                readOnly
                className="bg-slate-50"
              />
            </div>
            <div>
              <Label htmlFor="name">Node Name</Label>
              <Input
                id="name"
                value={newNodeName}
                onChange={(e) => setNewNodeName(e.target.value)}
                placeholder="Enter node name"
              />
            </div>
            <div>
              <Label htmlFor="data">Initial Data (optional)</Label>
              <Input
                id="data"
                value={newNodeData}
                onChange={(e) => setNewNodeData(e.target.value)}
                placeholder="Enter initial data"
              />
            </div>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setDialogOpen(false)}>
                Cancel
              </Button>
              <Button onClick={createNode}>
                Create Node
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </Sidebar>
  );
};
